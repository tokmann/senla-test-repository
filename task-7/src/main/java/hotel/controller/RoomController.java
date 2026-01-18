package task_11.controller;

import di.Component;
import di.Inject;
import task_11.controller.interfaces.IRoomController;
import task_11.model.Room;
import task_11.service.interfaces.IRoomManager;
import task_11.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class RoomController implements IRoomController {

    @Inject
    private IRoomManager roomManager;

    @Override
    public List<Room> getAllRooms(RoomSortOption option) {
        return roomManager.getSortedRooms(option);
    }

    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        return roomManager.getFreeRooms(option);
    }

    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return roomManager.findRoomsThatWillBeFree(date);
    }

    @Override
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        return roomManager.findRoomByNumber(roomNumber)
                .map(roomManager::fullRoomPrice);
    }

    @Override
    public List<String> getRoomHistory(int roomNumber) {
        return roomManager.getRoomHistory(roomNumber);
    }

    @Override
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        return roomManager.findRoomByNumber(roomNumber);
    }

    @Override
    public int countFreeRooms() {
        return roomManager.countFreeRooms();
    }

    @Override
    public boolean addRoom(Room room) {
        return roomManager.addRoom(room);
    }

    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        roomManager.setRoomMaintenance(roomNumber, maintenance);
    }
}
