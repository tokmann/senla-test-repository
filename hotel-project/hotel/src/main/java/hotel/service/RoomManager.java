package hotel.service;

import hotel.db.dao.jpa.JpaGuestDao;
import hotel.db.dao.jpa.JpaRoomDao;
import hotel.db.dao.jpa.JpaStayHistoryDao;
import hotel.exceptions.ValidationException;
import hotel.exceptions.guests.GuestNotCheckedInException;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.exceptions.rooms.RoomCapacityExceededException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.exceptions.rooms.RoomOccupiedException;
import hotel.exceptions.rooms.RoomUnderMaintenanceException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.service.interfaces.IRoomManager;
import hotel.config.RoomConfigurationService;
import hotel.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер для управления комнатами отеля.
 * Содержит бизнес-логику для добавления, заселения, выселения гостей,
 * управления статусами и историей комнат.
 */
@Transactional
@Service
public class RoomManager implements IRoomManager {

    private static final Logger log = LoggerFactory.getLogger(RoomManager.class);

    private final JpaRoomDao roomRepository;
    private final JpaStayHistoryDao stayHistoryRepository;
    private final RoomConfigurationService roomConfig;

    public RoomManager(JpaRoomDao roomRepository,
                       JpaStayHistoryDao stayHistoryRepository,
                       RoomConfigurationService roomConfig) {
        this.roomRepository = roomRepository;
        this.stayHistoryRepository = stayHistoryRepository;
        this.roomConfig = roomConfig;
    }

    /**
     * Добавляет новую комнату в систему.
     * @param room комната для добавления
     * @return true, если комната успешно добавлена, false если комната с таким номером уже существует
     */
    @Override
    public boolean addRoom(Room room) {
        log.info("Начало обработки команды: addRoom, room={}", room);
        validateRoom(room);

        Room existingRoom = roomRepository.findByNumber(room.getNumber());
        if (existingRoom != null) {
            log.error("Ошибка выполнения команды: addRoom - комната с номером {} уже существует", room.getNumber());
            return false;
        }

        roomRepository.save(room);
        log.info("Успешно выполнена команда: addRoom, room={}", room);
        return true;
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
        log.info("Начало обработки команды: checkIn, roomNumber={}, guestsCount={}", roomNumber, guests.size());
        validateCheckIn(guests, checkInDate, checkOutDate);

        Room room = roomRepository.findByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        if (room.isUnderMaintenance()) {
            throw new RoomUnderMaintenanceException(roomNumber);
        }

        if (room.isOccupied()) {
            throw new RoomOccupiedException(roomNumber);
        }

        if (guests.size() > room.getCapacity()) {
            throw new RoomCapacityExceededException(roomNumber, room.getCapacity(), guests.size());
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

        log.info("Успешно выполнена команда: checkIn, roomNumber={}, guestsCount={}", roomNumber, guests.size());
        return true;
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     * @param roomNumber номер комнаты
     * @return true, если выселение успешно, false если комната пуста или не найдена
     */
    @Override
    public boolean checkOut(int roomNumber) {
        return checkOutGuestFromRoom(roomNumber, null);
    }

    /**
     * Выселяет конкретного гостя из комнаты.
     * @param roomNumber номер комнаты
     * @return true, если выселение успешно, false если комната пуста или не найдена
     */
    @Override
    public boolean checkOut(int roomNumber, long guestId) {
        return checkOutGuestFromRoom(roomNumber, guestId);
    }

    private boolean checkOutGuestFromRoom(int roomNumber, Long guestId) {
        log.info("Начало обработки команды: checkOutGuestFromRoom, roomNumber={}, guestId={}", roomNumber, guestId);

        Room room = roomRepository.findByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        if (!room.isOccupied()) {
            throw new GuestNotCheckedInException(roomNumber);
        }

        List<Guest> guests = room.getGuests();

        if (guestId != null) {
            Guest guestToRemove = guests.stream()
                    .filter(g -> g.getId() == guestId)
                    .findFirst()
                    .orElse(null);

            if (guestToRemove == null) {
                throw new GuestNotFoundException(guestId);
            }

            guestToRemove.setRoom(null);
            guests.remove(guestToRemove);

            if (guests.isEmpty()) {
                room.setGuests(new ArrayList<>());
                room.setOccupied(false);
                room.clearOccupationTime();
            }

            String entry = "Выселен гость ID " + guestId + " " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            stayHistoryRepository.addEntry(room.getId(), entry);
        } else {
            for (Guest guest : guests) {
                guest.setRoom(null);
            }
            room.setGuests(new ArrayList<>());
            room.setOccupied(false);
            room.clearOccupationTime();

            String entry = "Выселены все гости " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            stayHistoryRepository.addEntry(room.getId(), entry);
        }

        roomRepository.save(room);
        log.info("Успешно выполнена команда: checkOutGuestFromRoom, roomNumber={}, guestId={}", roomNumber, guestId);
        return true;
    }

    /**
     * Устанавливает статус технического обслуживания для комнаты.
     * @param roomNumber номер комнаты
     * @param maintenance true, если требуется обслуживание, false в противном случае
     */
    @Override
    public boolean setRoomMaintenance(int roomNumber, boolean maintenance) {
        log.info("Начало обработки команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);

        Room room = roomRepository.findByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        if (!roomConfig.isStatusChangeEnabled()) {
            throw new IllegalStateException("Изменение статуса номера отключено в конфигурации");
        }

        if (room.isOccupied() && maintenance) {
            throw new RoomOccupiedException(roomNumber);
        }

        room.setUnderMaintenance(maintenance);
        roomRepository.save(room);

        log.info("Успешно выполнена команда: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);
        return true;
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
            throw new ValidationException("Цена комнаты не может быть отрицательной");
        }

        Room room = roomRepository.findByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        room.setPrice(newPrice);
        roomRepository.save(room);

        log.info("Успешно выполнена команда: changeRoomPrice, roomNumber={}, newPrice={}", roomNumber, newPrice);
    }

    /**
     * Возвращает список комнат, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список комнат
     */
    @Override
    public List<Room> getSortedRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getSortedRooms, option={}", option);
        List<Room> rooms = getAllRooms();
        return rooms.stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех комнат с загруженными связанными данными.
     * @return список всех комнат
     */
    @Override
    public List<Room> getAllRooms() {
        log.info("Начало обработки команды: getAllRooms");
        return roomRepository.findAll();
    }

    /**
     * Находит комнату по номеру.
     * @param roomNumber номер комнаты
     * @return Optional с комнатой или пустой Optional, если комната не найдена
     */
    @Override
    public Room findRoomByNumber(int roomNumber) {
        log.info("Начало обработки команды: findRoomByNumber, roomNumber={}", roomNumber);
        return roomRepository.findByNumber(roomNumber);
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
        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает количество свободных комнат.
     * @return количество свободных комнат
     */
    @Override
    public int countFreeRooms() {
        log.info("Начало обработки команды: countFreeRooms");
        return roomRepository.countFree();
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
            throw new ValidationException("Дата не может быть пустой");
        }
        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() ||
                        (room.getCheckOutDate() != null && !room.getCheckOutDate().isAfter(date)))
                .collect(Collectors.toList());
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
            throw new ValidationException("Комната не может быть пустой");
        }

        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) {
            return 0.0;
        }

        long days = ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
        return Math.max(1, days) * room.getPrice();
    }

    /**
     * Возвращает историю проживания для указанной комнаты.
     * @param roomNumber номер комнаты
     * @return история проживания
     */
    @Override
    public List<String> getRoomHistory(int roomNumber) {
        log.info("Начало обработки команды: getRoomHistory, roomNumber={}", roomNumber);

        Room room = roomRepository.findByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }

        return stayHistoryRepository.findByRoomId(room.getId(), roomConfig.getHistorySize());
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