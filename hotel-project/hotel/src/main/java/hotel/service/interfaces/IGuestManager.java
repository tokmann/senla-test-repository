package hotel.service.interfaces;

import hotel.model.Guest;
import hotel.model.Service;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IGuestManager {

    void addGuest(Guest guest);
    void removeGuest(Guest guest);
    List<Guest> getAllGuests();
    List<Guest> getGuestsNotCheckedIn();
    List<Guest> getGuestsCheckedIn();
    int countGuests();
    List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option);
    List<Guest> getSortedGuests(GuestSortOption option);
    Optional<Guest> getGuestById(long id);
    Guest findGuestByFullName(String fullName);
    void addServiceToGuest(long guestId, long serviceId);
    boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut);
    void checkOutGuest(long guestId);
    void addServiceToGuestByName(String guestFullName, String serviceName);
}
