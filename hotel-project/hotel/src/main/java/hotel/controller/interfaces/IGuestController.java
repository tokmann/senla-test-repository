package hotel.controller.interfaces;

import hotel.model.Guest;
import hotel.model.Service;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;


public interface IGuestController {

    Guest registerGuest(Guest guest);
    boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut);
    void checkOutGuest(long guestId);
    List<Guest> getSortedGuests(GuestSortOption option);
    Guest findGuestByFullName(String fullName);
    List<Service> getGuestServices(Guest guest, ServiceSortOption option);
    int countGuests();
    void addServiceToGuestByName(String guestFullName, String serviceName);

}
