package hotel.service;

import di.Component;
import di.Inject;
import hotel.db.TransactionManager;
import hotel.db.dao.jpa.JpaGuestDao;
import hotel.db.dao.jpa.JpaRoomDao;
import hotel.db.dao.jpa.JpaStayHistoryDao;
import hotel.exceptions.ValidationException;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.service.interfaces.IRoomManager;
import hotel.util.RoomConfigurationService;
import hotel.view.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private JpaRoomDao roomRepository;

    @Inject
    private JpaGuestDao guestRepository;

    @Inject
    private JpaStayHistoryDao stayHistoryRepository;

    @Inject
    private TransactionManager transactionManager;

    @Inject
    private  RoomConfigurationService roomConfig;

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
            Optional<Room> existingRoom = roomRepository.findByNumber(room.getNumber());
            if (existingRoom.isPresent()) {
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
        log.info("Начало обработки команды: checkIn, roomNumber={}, guestsCount={}, checkIn={}, checkOut={}",
                roomNumber, guests.size(), checkInDate, checkOutDate);

        validateCheckIn(guests, checkInDate, checkOutDate);

        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            if (!roomOpt.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkIn - комната {} не найдена", roomNumber);
                throw new RoomNotFoundException(roomNumber);
            }

            Room room = roomOpt.get();

            if (room.isUnderMaintenance()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkIn - комната {} на обслуживании", roomNumber);
                return false;
            }

            if (room.isOccupied()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkIn - комната {} уже занята", roomNumber);
                return false;
            }

            if (guests.size() > room.getCapacity()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkIn - превышена вместимость комнаты {}. Текущая вместимость {}, попытка заселить {} гостей",
                        roomNumber, room.getCapacity(), guests.size());
                return false;
            }

            for (Guest guest : guests) {
                guest.setRoom(room);
            }

            room.setGuests(guests);
            room.setOccupied(true);
            room.setCheckInDate(checkInDate);
            room.setCheckOutDate(checkOutDate);

            roomRepository.save(room);

            String guestNames = guests.stream()
                    .map(Guest::getFullName)
                    .collect(Collectors.joining(", "));
            String entry = "Гости: " + guestNames + " проживали с " + checkInDate + " по " + checkOutDate;
            stayHistoryRepository.addEntry(room.getId(), entry);

            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: checkIn, roomNumber={}, guestsCount={}", roomNumber, guests.size());
            return true;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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

        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            if (!roomOpt.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkOut - комната {} не найдена", roomNumber);
                throw new RoomNotFoundException(roomNumber);
            }

            Room room = roomOpt.get();

            if (!room.isOccupied()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkOut - комната {} не занята", roomNumber);
                return false;
            }

            List<Guest> guests = room.getGuests();
            for (Guest guest : guests) {
                guest.setRoom(null);
            }

            room.setGuests(new ArrayList<>());
            room.setOccupied(false);
            room.clearOccupationTime();

            roomRepository.save(room);

            String entry = "Выселены все гости " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            stayHistoryRepository.addEntry(room.getId(), entry);

            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: checkOut, roomNumber={}, guestsCount={}", roomNumber, guests.size());
            return true;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            if (!roomOpt.isPresent()) {
                log.error("Ошибка выполнения команды: setRoomMaintenance - комната {} не найдена", roomNumber);
                throw new RoomNotFoundException(roomNumber);
            }

            Room room = roomOpt.get();

            if (!roomConfig.isStatusChangeEnabled()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: setRoomMaintenance - изменение статуса номера отключено в конфигурации для комнаты {}", roomNumber);
                throw new IllegalStateException("Изменение статуса номера отключено в конфигурации");
            }

            if (room.isOccupied()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: setRoomMaintenance - комната {} занята", roomNumber);
                throw new RoomException("Нельзя установить обслуживание для занятой комнаты");
            }

            room.setUnderMaintenance(maintenance);
            roomRepository.save(room);

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
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            if (!roomOpt.isPresent()) {
                log.error("Ошибка выполнения команды: changeRoomPrice - комната {} не найдена", roomNumber);
                throw new RoomNotFoundException(roomNumber);
            }

            Room room = roomOpt.get();
            room.setPrice(newPrice);
            roomRepository.save(room);

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

        try {
            List<Room> rooms = getAllRooms();
            List<Room> sortedRooms = rooms.stream()
                    .sorted(option.getComparator())
                    .collect(Collectors.toList());

            log.info("Успешно выполнена команда: getSortedRooms, sortedRoomsCount={}", sortedRooms.size());
            return sortedRooms;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: getSortedRooms, option={}", option, e);
            throw new RoomException("Ошибка при сортировке комнат", e);
        }
    }

    /**
     * Возвращает список всех комнат с загруженными связанными данными.
     * @return список всех комнат
     */
    @Override
    public List<Room> getAllRooms() {
        log.info("Начало обработки команды: getAllRooms");

        try {
            List<Room> rooms = roomRepository.findAll();
            log.info("Успешно выполнена команда: getAllRooms, roomsCount={}", rooms.size());
            return rooms;
        } catch (Exception e) {
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

        try {
            Optional<Room> room = roomRepository.findByNumber(roomNumber);
            if (room.isPresent()) {
                log.info("Успешно выполнена команда: findRoomByNumber, roomNumber={}, found=true", roomNumber);
            } else {
                log.info("Успешно выполнена команда: findRoomByNumber, roomNumber={}, found=false", roomNumber);
            }
            return room;
        } catch (Exception e) {
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

        try {
            List<Room> freeRooms = getAllRooms().stream()
                    .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                    .sorted(option.getComparator())
                    .collect(Collectors.toList());

            log.info("Успешно выполнена команда: getFreeRooms, freeRoomsCount={}", freeRooms.size());
            return freeRooms;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: getFreeRooms, option={}", option, e);
            throw new RoomException("Ошибка при получении списка свободных комнат", e);
        }
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

        try {
            List<Room> allRooms = getAllRooms();
            List<Room> freeRooms = allRooms.stream()
                    .filter(room -> !room.isOccupied() ||
                            (room.getCheckOutDate() != null && !room.getCheckOutDate().isAfter(date)))
                    .collect(Collectors.toList());

            log.info("Успешно выполнена команда: findRoomsThatWillBeFree, freeRoomsCount={}", freeRooms.size());
            return freeRooms;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: findRoomsThatWillBeFree, date={}", date, e);
            throw new RoomException("Ошибка при поиске комнат, которые будут свободны к указанной дате", e);
        }
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

        long days = ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
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
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            if (!roomOpt.isPresent()) {
                log.error("Ошибка выполнения команды: getRoomHistory - комната {} не найдена", roomNumber);
                throw new RoomNotFoundException(roomNumber);
            }

            Room room = roomOpt.get();
            List<String> history = stayHistoryRepository.findByRoomId(room.getId(), roomConfig.getHistorySize());

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
}