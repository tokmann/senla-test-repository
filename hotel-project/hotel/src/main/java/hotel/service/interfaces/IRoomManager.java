package hotel.service.interfaces;

import hotel.model.Guest;
import hotel.model.Room;
import hotel.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface IRoomManager {

    boolean addRoom(Room room);
    boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate);
    boolean checkOut(int roomNumber);
    void setRoomMaintenance(int roomNumber, boolean maintenance);
    void changeRoomPrice(int roomNumber, double newPrice);
    List<Room> getSortedRooms(RoomSortOption option);
    List<Room> getAllRooms();
    Optional<Room> findRoomByNumber(int roomNumber);
    List<Room> getFreeRooms(RoomSortOption option);
    int countFreeRooms();
    List<Room> findRoomsThatWillBeFree(LocalDate date);
    double fullRoomPrice(Room room);
    List<String> getRoomHistory(int roomNumber);

}
