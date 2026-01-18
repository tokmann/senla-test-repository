package hotel.service;

import di.Component;
import di.Inject;
import hotel.db.TransactionManager;
import hotel.db.interfaces.GuestRepository;
import hotel.db.interfaces.StayHistoryRepository;
import hotel.exceptions.ValidationException;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.db.interfaces.RoomRepository;
import hotel.service.interfaces.IRoomManager;
import hotel.view.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Менеджер для управления комнатами отеля.
 * Содержит бизнес-логику для добавления, заселения, выселения гостей,
 * управления статусами и историей комнат.
 */
@Component
public class RoomManager implements IRoomManager {

    private static final Logger log = LoggerFactory.getLogger(RoomManager.class);

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private GuestRepository guestRepository;

    @Inject
    private StayHistoryRepository stayHistoryRepository;

    @Inject
    private TransactionManager transactionManager;

    /**
     * Добавляет новую комнату в систему.
     * @param room комната для добавления
     * @return true, если комната успешно добавлена, false если комната с таким номером уже существует
     */
    @Override
    public boolean addRoom(Room room) {
        log.info("Начало обработки команды: addRoom, room={}", room);
        validateRoom(room);

        transactionManager.beginTransaction();
        try {
            if (room.getId() != 0) {
                Optional<Room> existing = roomRepository.findById(room.getId());
                if (existing.isPresent()) {
                    updateExistingRoom(existing.get(), room);
                    transactionManager.commitTransaction();
                    log.info("Успешно выполнена команда: addRoom - обновлена существующая комната, roomId={}", room.getId());
                    return true;
                }
            }

            Optional<Room> existingByNumber = roomRepository.findByNumber(room.getNumber());
            if (existingByNumber.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: addRoom - комната с номером {} уже существует", room.getNumber());
                return false;
            }

            roomRepository.save(room);
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: addRoom, room={}", room);
            return true;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: addRoom, room={}", room, e);
            throw new RoomException("Ошибка при добавлении комнаты", e);
        }
    }

    /**
     * Заселяет гостей в указанную комнату на заданный период.
     * @param roomNumber номер комнаты
     * @param guests список гостей для заселения
     * @param checkInDate дата заселения
     * @param checkOutDate дата выселения
     * @return true, если заселение успешно, false в противном случае
     */
    @Override
    public boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        log.info("Начало обработки команды: checkIn, roomNumber={}, guests={}, checkIn={}, checkOut={}",
                roomNumber, guests, checkInDate, checkOutDate);
        validateCheckIn(guests, checkInDate, checkOutDate);

        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            if (room.isUnderMaintenance() || room.isOccupied()) {
                log.error("Ошибка выполнения команды: checkIn - комната {} занята или на обслуживании", roomNumber);
                return false;
            }

            if (guests.size() > room.getCapacity()) {
                log.error("Ошибка выполнения команды: checkIn - превышена вместимость комнаты {}", roomNumber);
                return false;
            }

            boolean success = room.checkIn(guests, checkInDate, checkOutDate);
            if (!success) {
                log.error("Ошибка выполнения команды: checkIn - не удалось заселить гостей в комнату {}", roomNumber);
                return false;
            }

            roomRepository.save(room);

            for (Guest guest : guests) {
                guest.setRoom(room);
                guest.setRoomId(room.getId());
                guestRepository.save(guest);
            }

            String entry = String.format("Заселено %d гостей с %s по %s",
                    guests.size(),
                    checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            log.info("Успешно выполнена команда: checkIn, roomNumber={}, guestsCount={}", roomNumber, guests.size());
            return true;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: checkIn, roomNumber={}", roomNumber, e);
            throw new RoomException("Ошибка при заселении гостей в комнату", e);
        }
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     * @param roomNumber номер комнаты
     * @return true, если выселение успешно, false если комната пуста или не найдена
     */
    @Override
    public boolean checkOut(int roomNumber) {
        log.info("Начало обработки команды: checkOut, roomNumber={}", roomNumber);

        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            if (!room.isOccupied()) {
                log.error("Ошибка выполнения команды: checkOut - комната {} пуста", roomNumber);
                return false;
            }

            List<Guest> guests = guestRepository.findByRoomId(room.getId());

            for (Guest guest : guests) {
                guest.setRoom(null);
                guest.setRoomId(null);
                guestRepository.save(guest);
            }

            room.checkOut();
            roomRepository.save(room);

            String entry = String.format("Выселены все гости %s", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            log.info("Успешно выполнена команда: checkOut, roomNumber={}, guestsCount={}", roomNumber, guests.size());
            return true;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: checkOut, roomNumber={}", roomNumber, e);
            throw new RoomException("Ошибка при выселении гостей из комнаты", e);
        }
    }

    /**
     * Устанавливает статус технического обслуживания для комнаты.
     * @param roomNumber номер комнаты
     * @param maintenance true, если требуется обслуживание, false в противном случае
     */
    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        log.info("Начало обработки команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);

        transactionManager.beginTransaction();
        try {
            roomRepository.findByNumber(roomNumber).ifPresent(room -> {
                room.setMaintenance(maintenance);
                roomRepository.save(room);
            });

            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance, e);
            throw new RoomException("Ошибка при изменении статуса обслуживания комнаты", e);
        }
    }

    /**
     * Изменяет цену комнаты.
     * @param roomNumber номер комнаты
     * @param newPrice новая цена
     */
    @Override
    public void changeRoomPrice(int roomNumber, double newPrice) {
        log.info("Начало обработки команды: changeRoomPrice, roomNumber={}, newPrice={}", roomNumber, newPrice);

        if (newPrice < 0) {
            log.error("Ошибка выполнения команды: changeRoomPrice - цена отрицательная, roomNumber={}, newPrice={}", roomNumber, newPrice);
            throw new ValidationException("Цена комнаты не может быть отрицательной");
        }

        transactionManager.beginTransaction();
        try {
            roomRepository.findByNumber(roomNumber).ifPresent(room -> {
                room.setPrice(newPrice);
                roomRepository.save(room);
            });
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: changeRoomPrice, roomNumber={}, newPrice={}", roomNumber, newPrice);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: changeRoomPrice, roomNumber={}, newPrice={}", roomNumber, newPrice, e);
            throw new RoomException("Ошибка при изменении цены комнаты", e);
        }
    }

    /**
     * Возвращает список комнат, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список комнат
     */
    @Override
    public List<Room> getSortedRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getSortedRooms, option={}", option);
        List<Room> rooms = getAllRooms().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getSortedRooms, sortedRoomsCount={}", rooms.size());
        return rooms;
    }

    /**
     * Возвращает список всех комнат с загруженными связанными данными.
     * @return список всех комнат
     */
    @Override
    public List<Room> getAllRooms() {
        log.info("Начало обработки команды: getAllRooms");

        transactionManager.beginTransaction();
        try {
            List<Room> rooms = roomRepository.findAll();
            rooms.forEach(room -> {
                List<Guest> guests = guestRepository.findByRoomId(room.getId());
                room.setGuests(guests);
                room.setOccupied(!guests.isEmpty());
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: getAllRooms, roomsCount={}", rooms.size());
            return rooms;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getAllRooms", e);
            throw new RoomException("Ошибка при получении списка комнат", e);
        }
    }

    /**
     * Находит комнату по номеру.
     * @param roomNumber номер комнаты
     * @return Optional с комнатой или пустой Optional, если комната не найдена
     */
    @Override
    public Optional<Room> findRoomByNumber(int roomNumber) {
        log.info("Начало обработки команды: findRoomByNumber, roomNumber={}", roomNumber);
        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            roomOpt.ifPresent(room -> {
                List<Guest> guests = guestRepository.findByRoomId(room.getId());
                room.setGuests(guests);
                room.setOccupied(!guests.isEmpty());
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();

            if (roomOpt.isPresent()) {
                log.info("Успешно выполнена команда: findRoomByNumber, roomNumber={}, found=true", roomNumber);
            } else {
                log.info("Успешно выполнена команда: findRoomByNumber, roomNumber={}, found=false", roomNumber);
            }

            return roomOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: findRoomByNumber, roomNumber={}", roomNumber, e);
            throw new RoomNotFoundException(roomNumber);
        }
    }

    /**
     * Возвращает список свободных комнат, отсортированных по указанному критерию.
     * Свободные комнаты - это комнаты, которые не заняты и не находятся на обслуживании.
     * @param option критерий сортировки
     * @return отсортированный список свободных комнат
     */
    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getFreeRooms, option={}", option);
        List<Room> freeRooms = getAllRooms().stream()
                .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getFreeRooms, freeRoomsCount={}", freeRooms.size());
        return freeRooms;
    }

    /**
     * Возвращает количество свободных комнат.
     * @return количество свободных комнат
     */
    @Override
    public int countFreeRooms() {
        log.info("Начало обработки команды: countFreeRooms");

        transactionManager.beginTransaction();
        try {
            int count = roomRepository.countFree();
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: countFreeRooms, count={}", count);
            return count;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: countFreeRooms", e);
            throw new RoomException("Ошибка при подсчете свободных комнат", e);
        }
    }

    /**
     * Находит комнаты, которые будут свободны к указанной дате.
     * Включает уже свободные комнаты и те, которые освободятся к заданной дате.
     * @param date дата
     * @return список комнат, которые будут свободны к указанной дате
     */
    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        log.info("Начало обработки команды: findRoomsThatWillBeFree, date={}", date);

        if (date == null) {
            log.error("Ошибка выполнения команды: findRoomsThatWillBeFree - дата пустая");
            throw new ValidationException("Дата не может быть пустой");
        }

        List<Room> freeRooms = getAllRooms().stream()
                .filter(room -> !room.isOccupied() ||
                        (room.getCheckOutDate() != null && !room.getCheckOutDate().isAfter(date)))
                .collect(Collectors.toList());

        log.info("Успешно выполнена команда: findRoomsThatWillBeFree, freeRoomsCount={}", freeRooms.size());
        return freeRooms;
    }

    /**
     * Рассчитывает полную стоимость проживания в комнате.
     * @param room комната
     * @return полная стоимость проживания
     */
    @Override
    public double fullRoomPrice(Room room) {
        log.info("Начало обработки команды: fullRoomPrice, roomNumber={}", room != null ? room.getNumber() : null);

        if (room == null) {
            log.error("Ошибка выполнения команды: fullRoomPrice - комната пустая");
            throw new ValidationException("Комната не может быть пустой");
        }
        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) {
            log.info("Успешно выполнена команда: fullRoomPrice, roomNumber={}, price=0.0 (нет дат заселения)", room.getNumber());
            return 0.0;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
        double price = Math.max(1, days) * room.getPrice();
        log.info("Успешно выполнена команда: fullRoomPrice, roomNumber={}, price={}", room.getNumber(), price);
        return price;
    }

    /**
     * Возвращает историю проживания для указанной комнаты.
     * @param roomNumber номер комнаты
     * @return история проживания
     */
    @Override
    public List<String> getRoomHistory(int roomNumber) {
        log.info("Начало обработки команды: getRoomHistory, roomNumber={}", roomNumber);

        transactionManager.beginTransaction();
        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));
            List<String> history = stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize());
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: getRoomHistory, roomNumber={}, entriesCount={}", roomNumber, history.size());
            return history;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getRoomHistory, roomNumber={}", roomNumber, e);
            throw new RoomException("Ошибка при получении истории комнаты", e);
        }
    }

    /**
     * Валидирует данные комнаты перед сохранением.
     * @param room комната для валидации
     */
    private void validateRoom(Room room) {
        if (room.getNumber() <= 0) {
            throw new ValidationException("Номер комнаты должен быть положительным числом");
        }
        if (room.getCapacity() <= 0) {
            throw new ValidationException("Вместимость комнаты должна быть положительным числом");
        }
        if (room.getPrice() < 0) {
            throw new ValidationException("Цена комнаты не может быть отрицательной");
        }
        if (room.getStars() < 1 || room.getStars() > 5) {
            throw new ValidationException("Количество звезд должно быть от 1 до 5");
        }
    }

    /**
     * Валидирует данные для заселения гостей.
     * @param guests список гостей для заселения
     * @param checkIn дата заселения
     * @param checkOut дата выселения
     */
    private void validateCheckIn(List<Guest> guests, LocalDate checkIn, LocalDate checkOut) {
        if (guests == null || guests.isEmpty()) {
            throw new ValidationException("Список гостей не может быть пустым");
        }
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Даты заселения и выселения не могут быть пустыми");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Дата выселения должна быть после даты заселения");
        }
    }

    /**
     * Обновляет существующую комнату новыми данными.
     * @param existing существующая комната
     * @param newData новые данные для обновления
     */
    private void updateExistingRoom(Room existing, Room newData) {
        existing.setPrice(newData.getPrice());
        if (!existing.isOccupied()) {
            existing.setMaintenance(newData.isUnderMaintenance());
        }
    }
}