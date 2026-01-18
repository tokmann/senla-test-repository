package task_11.controller.interfaces;

import task_11.model.Guest;
import task_11.model.Service;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

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
