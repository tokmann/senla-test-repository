package task_3_4.management;

public class HotelManager {
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;

    public HotelManager() {
        this.roomManager = new RoomManager();
        this.serviceManager = new ServiceManager();
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void displayHotelStatus() {
        roomManager.displayAllRooms();
        serviceManager.displayAllServices();
    }
}