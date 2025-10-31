package task_4.management;

import task_4.model.Guest;
import task_4.model.Room;
import task_4.model.Service;

import java.time.LocalDate;
import java.util.List;

public class HotelManager {
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;
    private final GuestManager guestManager;

    public HotelManager() {
        this.serviceManager = new ServiceManager();
        this.guestManager = new GuestManager();
        this.roomManager = new RoomManager(guestManager);
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void addGuest(Guest guest) {
        guestManager.addGuest(guest);
    }

    public List<Guest> getGuests() {
        return guestManager.getGuests();
    }

    public void checkGuestsIn(int room, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        roomManager.checkIn(room, guests, checkInDate, checkOutDate);
    }

    public void checkGuestsOut(int room) {
        roomManager.checkOut(room);
    }

    // 1. Список всех номеров с сортировкой
    public void showAllRooms(String by) {
        roomManager.displayAllSortedRooms(by);
    }

    // 2. Список свободных номеров
    public void showAllFreeRooms(String by) {
        roomManager.displayAllFreeSortedRooms(by);
    }

    // 3. Список постояльцев по алфавиту
    public void showAllSortedGuests(String by) {
        guestManager.showAllSortedGuests(by);
    }

    // 4. Общее число свободных номеров
    public int countFreeRooms() {
        return roomManager.countFreeRooms();
    }

    // 5. Общее число постояльцев
    public int countGuests() {
        return guestManager.allGuestsCount();
    }

    // 6. Список номеров которые будут свободны по определенной дате
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return roomManager.findRoomsThatWillBeFree(date);
    }

    // 7. Сумма оплаты за номер, которую должен оплатить постоялец
    public double getFullRoomPrice(Room room) {
        return roomManager.fullRoomPrice(room);
    }

    // 8. Посмотреть до десяти последних постояльцев номера и даты их пребывания
    public void showLastThreeGuests(int roomNumber, int historyLength) {
        System.out.println(roomManager.showHistory(roomNumber, historyLength));
    }

    // 9. Посмотреть список услуг постояльца и их цену
    public List<Service> getSortedGuestServices(Guest guest, String by) {
        return guestManager.getSortedGuestServices(guest, by);
    }

    // 10. Цены всех услуг (сортировка номеров уже есть в номере 1)
    public List<Service> getSortedServices(String by) {
        return serviceManager.getAllSortedServices(by);
    }

    // 11. Посмотреть детали отдельного номера
    public void showFullRoomInfo(int roomNumber) {
        roomManager.showFullRoomInfo(roomNumber);
    }

}