package task_11.controller.interfaces;

import task_11.model.Room;
import task_11.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomController {

    List<Room> getAllRooms(RoomSortOption option);
    List<Room> getFreeRooms(RoomSortOption option);
    List<Room> findRoomsThatWillBeFree(LocalDate date);
    Optional<Double> getFullRoomPrice(int roomNumber);
    List<String> getRoomHistory(int roomNumber);
    Optional<Room> getFullRoomInfo(int roomNumber);
    int countFreeRooms();
    boolean addRoom(Room room);
    void setRoomMaintenance(int roomNumber, boolean maintenance);
}
