package task_11.db.interfaces;

import task_11.model.Guest;
import task_11.model.Service;

import java.util.List;

public interface GuestServiceRepository {
    void addServiceToGuest(long guestId, long serviceId);
    void removeServiceFromGuest(long guestId, long serviceId);
    List<Service> findServicesByGuestId(long guestId);
    List<Guest> findGuestsByServiceId(long serviceId);
}
