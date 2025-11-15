package task_6.io.importer;

import task_6.model.Guest;
import task_6.model.Service;
import task_6.service.GuestManager;
import task_6.service.ServiceManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceCsvImporter {

    private final ServiceManager serviceManager;
    private final GuestManager guestManager;

    public ServiceCsvImporter(ServiceManager serviceManager, GuestManager guestManager) {
        this.serviceManager = serviceManager;
        this.guestManager = guestManager;
    }

    public void importFromCsv(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);

                long id = Long.parseLong(p[0]);
                String name = p[1];
                String description = p[2];
                double price = Double.parseDouble(p[3]);
                LocalDate date = LocalDate.parse(p[4]);
                List<Long> guestIds = p.length > 5 ? parseGuestIds(p[5]) : new ArrayList<>();

                Service service = serviceManager.getServiceById(id);
                if (service != null) {
                    updateService(service, name, description, price, date);
                    System.out.println("Обновлена услуга: " + name);
                } else {
                    service = new Service(id, name, description, price, date);
                    serviceManager.addService(service);
                    System.out.println("Добавлена услуга: " + name);
                }

                if (!guestIds.isEmpty()) {
                    restoreServiceGuests(service, guestIds);
                }
            }
        }
    }

    private List<Long> parseGuestIds(String guestIdsStr) {
        List<Long> guestIds = new ArrayList<>();
        if (guestIdsStr != null && !guestIdsStr.isBlank()) {
            String[] ids = guestIdsStr.split(";");
            for (String id : ids) {
                if (!id.isBlank()) {
                    guestIds.add(Long.parseLong(id));
                }
            }
        }
        return guestIds;
    }

    private void updateService(Service service, String name, String description, double price, LocalDate date) {
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDate(date);
    }

    /**
     * Восстанавливаем связи услуги с гостями
     */
    private void restoreServiceGuests(Service service, List<Long> guestIds) {
        for (Long guestId : guestIds) {
            Optional<Guest> guestOpt = guestManager.getGuestById(guestId);
            if (guestOpt.isPresent()) {
                Guest guest = guestOpt.get();
                if (!guest.getGuestServices().contains(service)) {
                    guestManager.addServiceToGuest(guest, service);
                }
            } else {
                System.out.println("Гость с ID " + guestId + " не найден для услуги " + service.getName());
            }
        }
    }

}
