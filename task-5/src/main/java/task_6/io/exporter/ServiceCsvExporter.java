package task_6.io.exporter;

import task_6.model.Guest;
import task_6.model.Service;
import task_6.repository.interfaces.ServiceRepository;
import task_6.service.GuestManager;
import task_6.service.ServiceManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceCsvExporter {

    private final ServiceManager serviceManager;
    private final GuestManager guestManager;

    public ServiceCsvExporter(ServiceManager serviceManager, GuestManager guestManager) {
        this.serviceManager = serviceManager;
        this.guestManager = guestManager;
    }

    public void exportToCsv(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,name,description,price,date,guestIds\n");

            for (Service service : serviceManager.getAllServices()) {
                List<Guest> guestsWithService = guestManager.getAllGuests().stream()
                        .filter(guest -> guest.getGuestServices().contains(service))
                        .collect(Collectors.toList());

                String guestIds = guestsWithService.stream()
                        .map(guest -> String.valueOf(guest.getId()))
                        .collect(Collectors.joining(";"));

                writer.write(
                        service.getId() + "," +
                                service.getName() + "," +
                                service.getDescription() + "," +
                                service.getPrice() + "," +
                                service.getDate() + "," +
                                guestIds + "\n"
                );
            }
        }
    }
}
