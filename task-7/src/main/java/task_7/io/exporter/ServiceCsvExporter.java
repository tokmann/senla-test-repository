package task_7.io.exporter;

import task_7.io.interfaces.CsvExporter;
import task_7.model.Guest;
import task_7.model.Service;
import task_7.service.GuestManager;
import task_7.service.ServiceManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Экспортер данных услуг в CSV формат.
 * Сериализует информацию об услугах и их связи с гостями.
 */
public class ServiceCsvExporter implements CsvExporter {

    private final ServiceManager serviceManager;
    private final GuestManager guestManager;

    public ServiceCsvExporter(ServiceManager serviceManager, GuestManager guestManager) {
        this.serviceManager = serviceManager;
        this.guestManager = guestManager;
    }

    /**
     * Экспортирует все услуги в CSV файл.
     * Формат: id,name,description,price,date,guestIds
     * @param filePath путь для сохранения файла
     */
    @Override
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
