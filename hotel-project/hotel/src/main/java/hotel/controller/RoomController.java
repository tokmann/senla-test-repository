package hotel.controller;

import hotel.controller.interfaces.IRoomController;
import hotel.model.Room;
import hotel.service.interfaces.IRoomManager;
import hotel.view.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для обработки команд, связанных с номерами отеля.
 * Делегирует бизнес-логику в {@link IRoomManager}.
 */
@Component
public class RoomController implements IRoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    private final IRoomManager roomManager;

    public RoomController(IRoomManager roomManager) {
        this.roomManager = roomManager;
    }

    /**
     * Возвращает список всех номеров с сортировкой.
     * @param option параметр сортировки
     * @return список номеров
     */
    @Override
    public List<Room> getAllRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getAllRooms, option={}", option);
        return roomManager.getSortedRooms(option);
    }

    /**
     * Возвращает список свободных номеров.
     * @param option параметр сортировки
     * @return список свободных номеров
     */
    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getFreeRooms, option={}", option);
        return roomManager.getFreeRooms(option);
    }

    /**
     * Находит номера, которые будут свободны на указанную дату.
     * @param date дата
     * @return список номеров
     */
    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        log.info("Начало обработки команды: findRoomsThatWillBeFree, date={}", date);
        return roomManager.findRoomsThatWillBeFree(date);
    }

    /**
     * Рассчитывает полную стоимость проживания в номере.
     * @param roomNumber номер комнаты
     * @return стоимость проживания, если номер найден
     */
    @Override
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        log.info("Начало обработки команды: getFullRoomPrice, roomNumber={}", roomNumber);
        return roomManager.findRoomByNumber(roomNumber).map(roomManager::fullRoomPrice);
    }

    /**
     * Возвращает историю изменений состояния номера.
     * @param roomNumber номер комнаты
     * @return список записей истории
     */
    @Override
    public List<String> getRoomHistory(int roomNumber) {
        log.info("Начало обработки команды: getRoomHistory, roomNumber={}", roomNumber);
        return roomManager.getRoomHistory(roomNumber);
    }

    /**
     * Возвращает полную информацию о номере.
     * @param roomNumber номер комнаты
     * @return информация о номере
     */
    @Override
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        log.info("Начало обработки команды: getFullRoomInfo, roomNumber={}", roomNumber);
        return roomManager.findRoomByNumber(roomNumber);
    }

    /**
     * Возвращает количество свободных номеров.
     * @return количество свободных номеров
     */
    @Override
    public int countFreeRooms() {
        log.info("Начало обработки команды: countFreeRooms");
        return roomManager.countFreeRooms();
    }

    /**
     * Добавляет новый номер в систему.
     * @param room объект номера
     * @return true, если номер был добавлен
     */
    @Override
    public boolean addRoom(Room room) {
        log.info("Начало обработки команды: addRoom, room={}", room);
        return roomManager.addRoom(room);
    }

    /**
     * Устанавливает или снимает режим обслуживания для номера.
     * @param roomNumber  номер комнаты
     * @param maintenance признак обслуживания
     */
    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        log.info("Начало обработки команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);
        roomManager.setRoomMaintenance(roomNumber, maintenance);
    }
}
