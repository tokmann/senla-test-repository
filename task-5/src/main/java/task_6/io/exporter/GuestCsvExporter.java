package task_6.io.exporter;

import task_6.model.Guest;
import task_6.repository.interfaces.GuestRepository;
import task_6.service.GuestManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class GuestCsvExporter {

    private final GuestManager guestManager;

    public GuestCsvExporter(GuestManager guestManager) {
        this.guestManager = guestManager;
    }

    public void exportToCsv(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,age,firstName,secondName,roomId,serviceIds\n");

            for (Guest guest : guestManager.getAllGuests()) {
                String serviceIds = guest.getGuestServices().stream()
                        .map(service -> String.valueOf(service.getId()))
                        .collect(Collectors.joining(";"));

                long roomId = guest.getGuestRoom() != null ? guest.getGuestRoom().getId() : -1;

                writer.write(
                        guest.getId() + "," +
                                guest.getAge() + "," +
                                guest.getFirstName() + "," +
                                guest.getSecondName() + "," +
                                roomId + "," +
                                serviceIds + "\n"
                );
            }
        }
    }
}
