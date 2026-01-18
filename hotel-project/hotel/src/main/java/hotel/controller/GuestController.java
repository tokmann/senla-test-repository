package hotel.controller;

import di.Component;
import di.Inject;
import hotel.controller.interfaces.IGuestController;
import hotel.model.Guest;
import hotel.model.Service;
import hotel.service.interfaces.IGuestManager;
import hotel.service.interfaces.IRoomManager;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;

@Component
public class GuestController implements IGuestController {

    @Inject
    private IGuestManager guestManager;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    @Override
    public Guest registerGuest(Guest guest) {
        guestManager.addGuest(guest);
        return guest;
    }

    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return guestManager.checkInGuest(guestId, roomNumber, checkIn, checkOut);
    }

    @Override
    public void checkOutGuest(long guestId) {
        guestManager.checkOutGuest(guestId);
    }

    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return guestManager.getSortedGuests(option);
    }

    @Override
    public Guest findGuestByFullName(String fullName) {
        return guestManager.findGuestByFullName(fullName);
    }

    @Override
    public List<Service> getGuestServices(Guest guest, ServiceSortOption option) {
        return guestManager.getSortedGuestServices(guest, option);
    }

    @Override
    public int countGuests() {
        return guestManager.countGuests();
    }

    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        guestManager.addServiceToGuestByName(guestFullName, serviceName);
    }
}
