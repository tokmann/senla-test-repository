package hotel.controller;

import hotel.controller.interfaces.IGuestController;
import hotel.model.Guest;
import hotel.model.Service;
import hotel.service.interfaces.IGuestManager;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GuestController implements IGuestController {

    private static final Logger log = LoggerFactory.getLogger(GuestController.class);

    private final IGuestManager guestManager;

    public GuestController(IGuestManager guestManager) {
        this.guestManager = guestManager;
    }

    @Override
    public Guest registerGuest(Guest guest) {
        log.info("Начало обработки команды: registerGuest, guest={}", guest);
        guestManager.addGuest(guest);
        return guest;
    }

    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        log.info("Начало обработки команды: checkInGuest, guestId={}, roomNumber={}, checkIn={}, checkOut={}",
                guestId, roomNumber, checkIn, checkOut);
        return guestManager.checkInGuest(guestId, roomNumber, checkIn, checkOut);
    }

    @Override
    public void checkOutGuest(long guestId) {
        log.info("Начало обработки команды: checkOutGuest, guestId={}", guestId);
        guestManager.checkOutGuest(guestId);
    }

    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        log.info("Начало обработки команды: getSortedGuests, option={}", option);
        return guestManager.getSortedGuests(option);
    }

    @Override
    public Guest findGuestByFullName(String fullName) {
        log.info("Начало обработки команды: findGuestByFullName, fullName={}", fullName);
        return guestManager.findGuestByFullName(fullName);
    }

    @Override
    public List<Service> getGuestServices(Guest guest, ServiceSortOption option) {
        log.info("Начало обработки команды: getGuestServices, guest={}, option={}", guest, option);
        return guestManager.getSortedGuestServices(guest, option);
    }

    @Override
    public int countGuests() {
        log.info("Начало обработки команды: countGuests");
        return guestManager.countGuests();
    }

    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        log.info("Начало обработки команды: addServiceToGuestByName, fullName={}, service={}",
                guestFullName, serviceName);
        guestManager.addServiceToGuestByName(guestFullName, serviceName);
    }
}
