package hotel.service.interfaces;

import hotel.model.Guest;
import hotel.model.Service;
import hotel.enums.GuestSortOption;
import hotel.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;

public interface IGuestManager {

    Guest addGuest(Guest guest);
    void removeGuest(Guest guest);
    List<Guest> getAllGuests();
    List<Guest> getGuestsNotCheckedIn();
    List<Guest> getGuestsCheckedIn();
    int countGuests();
    List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option);
    List<Guest> getSortedGuests(GuestSortOption option);
    Guest getGuestById(long id);
    Guest findGuestByFullName(String fullName);
    boolean addServiceToGuest(long guestId, long serviceId);
    boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut);
    boolean checkOutGuest(long guestId);
    boolean addServiceToGuestByName(String guestFullName, String serviceName);
}
