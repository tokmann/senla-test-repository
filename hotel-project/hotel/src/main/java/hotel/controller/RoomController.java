package hotel.controller;

import di.Component;
import di.Inject;
import hotel.controller.interfaces.IRoomController;
import hotel.model.Room;
import hotel.service.interfaces.IRoomManager;
import hotel.view.enums.RoomSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class RoomController implements IRoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    @Inject
    private IRoomManager roomManager;

    @Override
    public List<Room> getAllRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getAllRooms, option={}", option);
        return roomManager.getSortedRooms(option);
    }

    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        log.info("Начало обработки команды: getFreeRooms, option={}", option);
        return roomManager.getFreeRooms(option);
    }

    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        log.info("Начало обработки команды: findRoomsThatWillBeFree, date={}", date);
        return roomManager.findRoomsThatWillBeFree(date);
    }

    @Override
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        log.info("Начало обработки команды: getFullRoomPrice, roomNumber={}", roomNumber);
        return roomManager.findRoomByNumber(roomNumber).map(roomManager::fullRoomPrice);
    }

    @Override
    public List<String> getRoomHistory(int roomNumber) {
        log.info("Начало обработки команды: getRoomHistory, roomNumber={}", roomNumber);
        return roomManager.getRoomHistory(roomNumber);
    }

    @Override
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        log.info("Начало обработки команды: getFullRoomInfo, roomNumber={}", roomNumber);
        return roomManager.findRoomByNumber(roomNumber);
    }

    @Override
    public int countFreeRooms() {
        log.info("Начало обработки команды: countFreeRooms");
        return roomManager.countFreeRooms();
    }

    @Override
    public boolean addRoom(Room room) {
        log.info("Начало обработки команды: addRoom, room={}", room);
        return roomManager.addRoom(room);
    }

    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        log.info("Начало обработки команды: setRoomMaintenance, roomNumber={}, maintenance={}", roomNumber, maintenance);
        roomManager.setRoomMaintenance(roomNumber, maintenance);
    }
}
