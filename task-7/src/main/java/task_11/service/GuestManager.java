package task_11.service;

import di.Component;
import di.Inject;
import task_11.db.TransactionManager;
import task_11.db.interfaces.GuestServiceRepository;
import task_11.db.interfaces.RoomRepository;
import task_11.exceptions.ValidationException;
import task_11.exceptions.guests.GuestAlreadyCheckedInException;
import task_11.exceptions.guests.GuestException;
import task_11.exceptions.guests.GuestNotCheckedInException;
import task_11.exceptions.guests.GuestNotFoundException;
import task_11.exceptions.services.ServiceNotFoundException;
import task_11.model.Guest;
import task_11.model.Room;
import task_11.model.Service;
import task_11.db.interfaces.GuestRepository;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GuestManager implements IGuestManager {

    @Inject
    private GuestRepository guestRepository;

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private GuestServiceRepository guestServiceRepository;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    @Inject
    private TransactionManager transactionManager;

    @Override
    public void addGuest(Guest guest) {
        transactionManager.beginTransaction();
        try {
            guestRepository.save(guest);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public void removeGuest(Guest guest) {
        if (guest == null) {
            throw new ValidationException("Guest cannot be null");
        }

        transactionManager.beginTransaction();
        try {
            Guest loadedGuest = guestRepository.findById(guest.getId())
                    .orElseThrow(() -> new GuestNotFoundException(guest.getId()));

            if (loadedGuest.getRoom() != null) {
                throw new GuestException("Cannot delete checked-in guest " + loadedGuest.getFullName() +
                        " from room " + loadedGuest.getRoom().getNumber());
            }

            guestRepository.delete(loadedGuest);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Guest> getAllGuests() {
        transactionManager.beginTransaction();
        try {
            List<Guest> guests = guestRepository.findAll();
            guests.forEach(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });
            transactionManager.commitTransaction();
            return guests;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Guest> getGuestsNotCheckedIn() {
        return getAllGuests().stream()
                .filter(guest -> guest.getRoom() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Guest> getGuestsCheckedIn() {
        return getAllGuests().stream()
                .filter(guest -> guest.getRoom() != null)
                .collect(Collectors.toList());
    }

    @Override
    public int countGuests() {
        transactionManager.beginTransaction();
        try {
            int count = guestRepository.count();
            transactionManager.commitTransaction();
            return count;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option) {
        if (guest == null) {
            throw new ValidationException("Guest cannot be null");
        }

        transactionManager.beginTransaction();
        try {
            List<Service> services = guestServiceRepository.findServicesByGuestId(guest.getId());
            transactionManager.commitTransaction();
            return services.stream()
                    .sorted(option.getComparator())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return getAllGuests().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Guest> getGuestById(long id) {
        transactionManager.beginTransaction();
        try {
            Optional<Guest> guestOpt = guestRepository.findById(id);
            guestOpt.ifPresent(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });
            transactionManager.commitTransaction();
            return guestOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public Guest findGuestByFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Guest full name cannot be empty");
        }

        String normalizedFullName = fullName.trim().toLowerCase();
        return getAllGuests().stream()
                .filter(guest -> guest.getFullName().toLowerCase().equals(normalizedFullName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addServiceToGuest(long guestId, long serviceId) {
        transactionManager.beginTransaction();
        try {
            guestServiceRepository.addServiceToGuest(guestId, serviceId);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Check-in and check-out dates cannot be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Check-out date must be after check-in date");
        }

        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));

            if (guest.getRoom() != null) {
                throw new GuestAlreadyCheckedInException(guestId);
            }

            boolean result = roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);

            if (result) {
                transactionManager.commitTransaction();
                return true;
            } else {
                transactionManager.rollbackTransaction();
                return false;
            }
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public void checkOutGuest(long guestId) {
        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));

            Room room = guest.getRoom();
            if (room == null) {
                throw new GuestNotCheckedInException(guestId);
            }

            roomManager.checkOut(room.getNumber());
            guest.setRoom(null);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        if (guestFullName == null || guestFullName.trim().isEmpty()) {
            throw new ValidationException("Guest full name cannot be empty");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Service name cannot be empty");
        }

        transactionManager.beginTransaction();
        try {
            Guest guest = findGuestByFullName(guestFullName);
            if (guest == null) {
                throw new GuestNotFoundException(guestFullName);
            }

            Service service = serviceManager.findByName(serviceName);
            if (service == null) {
                throw new ServiceNotFoundException(serviceName);
            }

            addServiceToGuest(guest.getId(), service.getId());
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }
}
