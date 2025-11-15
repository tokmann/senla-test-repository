package task_6.service;

import task_6.model.Guest;
import task_6.model.Room;
import task_6.repository.interfaces.RoomRepository;
import task_6.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервисный слой для управления номерами отеля.
 * Отвечает за бронирование, выселение, установку цен,
 * техническое обслуживание, историю и фильтрацию номеров.
 * Инкапсулирует бизнес-логику, связанную с сущностью {@link Room}.
 */
public class RoomManager {

    private final RoomRepository repository;
    private final GuestManager guestManager;

    public RoomManager(RoomRepository repository, GuestManager guestManager) {
        this.repository = repository;
        this.guestManager = guestManager;
    }

    /**
     * Добавляет номер в систему.
     * Если номер с таким ID уже существует - обновляет его.
     * Если номер с таким физическим номером уже существует - отклоняет добавление.
     * @param room комната для добавления
     * @return true если добавление/обновление успешно, false если номер уже существует
     */
    public boolean addRoom(Room room) {
        // Проверяем по ID для обновления
        if (room.getId() != 0) {
            Optional<Room> existingById = repository.findById(room.getId());
            if (existingById.isPresent()) {
                updateExistingRoom(existingById.get(), room);
                return true;
            }
        }
        // Проверяем по номеру для новых комнат
        Optional<Room> existingByNumber = repository.findByNumber(room.getNumber());
        if (existingByNumber.isPresent()) {
            return false;
        }
        repository.save(room);
        return true;
    }

    /**
     * Заселяет гостей в номер на указанный период.
     * Обновляет связи между гостями и номером.
     * @param roomNumber номер комнаты для заселения
     * @param guests список гостей для заселения
     * @param checkInDate дата заселения
     * @param checkOutDate дата выселения
     * @return true если заселение успешно, false если комната не найдена или недоступна
     */
    public boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        Optional<Room> optionalRoom = repository.findByNumber(roomNumber);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            boolean ok = room.checkIn(guests, checkInDate, checkOutDate);
            if (!ok) return false;
            guests.forEach(guest -> guest.setGuestRoom(room));
            return true;
        }
        return false;
    }

    /**
     * Выселяет всех гостей из указанного номера.
     * Удаляет гостей из репозитория.
     * @param roomNumber номер комнаты для выселения
     * @return true если выселение успешно, false если комната не найдена
     */
    public boolean checkOut(int roomNumber) {
        Optional<Room> optionalRoom = repository.findByNumber(roomNumber);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            List<Guest> guests = List.copyOf(room.getGuests());
            room.checkOut();
            guests.forEach(guestManager::removeGuest);
            return true;
        }
        return false;
    }

    /**
     * Помечает номер как находящийся на обслуживании.
     * Номер можно перевести на обслуживание только если он свободен.
     * @param roomNumber номер комнаты
     * @param maintenance true для установки на обслуживание, false для снятия
     */
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        repository.findByNumber(roomNumber).ifPresent(room -> room.setMaintenance(maintenance));
    }

    /**
     * Изменяет цену номера.
     * @param roomNumber номер комнаты
     * @param newPrice новая цена за сутки
     */
    public void changeRoomPrice(int roomNumber, double newPrice) {
        repository.findByNumber(roomNumber).ifPresent(room -> room.setPrice(newPrice));
    }

    /**
     * Возвращает список комнат, отсортированный по указанному критерию.
     * @param option критерий сортировки комнат
     * @return отсортированный список комнат
     */
    public List<Room> getSortedRooms(RoomSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все комнаты системы.
     * @return список всех комнат
     */
    public List<Room> getAllRooms() {
        return repository.findAll();
    }

    /**
     * Находит комнату по физическому номеру.
     * @param roomNumber физический номер комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    public Optional<Room> findRoomByNumber(int roomNumber) {
        return repository.findByNumber(roomNumber);
    }

    /**
     * Возвращает список всех свободных и не находящихся на обслуживании номеров.
     * @param option критерий сортировки результатов
     * @return отсортированный список свободных комнат
     */
    public List<Room> getFreeRooms(RoomSortOption option) {
        return repository.findAll().stream()
                .filter(r -> !r.isOccupied() && !r.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Подсчитывает количество свободных номеров.
     * @return количество свободных номеров
     */
    public int countFreeRooms() {
        return (int) repository.findAll().stream()
                .filter(r -> !r.isOccupied())
                .count();
    }

    /**
     * Возвращает список номеров, которые освободятся к указанной дате.
     * Включает уже свободные номера и номера, которые будут свободны к дате.
     * @param date дата для проверки доступности
     * @return список доступных к дате номеров
     */
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return repository.findAll().stream()
                .filter(r -> !r.isOccupied() ||
                        (r.getCheckOutDate() != null &&
                                (r.getCheckOutDate().isBefore(date) || r.getCheckOutDate().isEqual(date))))
                .collect(Collectors.toList());
    }

    /**
     * Вычисляет полную стоимость проживания в номере по количеству дней.
     * Минимальная продолжительность проживания - 1 день.
     * @param room комната для расчета стоимости
     * @return общая стоимость проживания
     */
    public double fullRoomPrice(Room room) {
        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) return 0.0;
        long days = ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
        if (days <= 0) days = 1;
        return days * room.getPrice();
    }

    /**
     * Возвращает историю последних заселений в номер.
     * @param roomNumber номер комнаты
     * @param historyLength количество последних записей для возврата
     * @return список строк с историей проживания
     */
    public List<String> getRoomHistory(int roomNumber, int historyLength) {
        return repository.findByNumber(roomNumber)
                .map(room -> room.getLastStays(historyLength))
                .orElse(List.of());
    }

    /**
     * Находит комнату по идентификатору.
     * @param id идентификатор комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    public Optional<Room> findRoomById(long id) {
        return repository.findById(id);
    }

    /**
     * Обновляет данные существующей комнаты.
     * Изменяет цену и статус обслуживания, если комната свободная.
     * @param existing существующая комната
     * @param newData новые данные
     */
    private void updateExistingRoom(Room existing, Room newData) {
        existing.setPrice(newData.getPrice());
        if (!existing.isOccupied()) {
            existing.setMaintenance(newData.isUnderMaintenance());
        }
        System.out.println("Обновлена комната ID: " + existing.getId() + ", номер: " + existing.getNumber());
    }

}