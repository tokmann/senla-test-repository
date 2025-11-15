package task_6.io.exporter;

import task_6.io.interfaces.CsvExporter;
import task_6.model.Room;
import task_6.service.RoomManager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Экспортер данных комнат в CSV формат.
 * Сериализует информацию о комнатах, включая историю проживания и текущих гостей.
 */
public class RoomCsvExporter implements CsvExporter  {

    private final RoomManager roomManager;

    public RoomCsvExporter(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    /**
     * Экспортирует все комнаты в CSV файл.
     * Формат: id,number,capacity,price,stars,isOccupied,underMaintenance,checkInDate,checkOutDate,guestIds
     * @param filePath путь для сохранения файла
     */
    @Override
    public void exportToCsv(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,number,capacity,price,stars,isOccupied,underMaintenance,checkInDate,checkOutDate,guestIds\n");

            for (Room room : roomManager.getAllRooms()) { // Добавим этот метод в RoomManager
                String guestIds = room.getGuests().stream()
                        .map(guest -> String.valueOf(guest.getId()))
                        .collect(Collectors.joining(";"));

                writer.write(
                        room.getId() + "," +
                                room.getNumber() + "," +
                                room.getCapacity() + "," +
                                room.getPrice() + "," +
                                room.getStars() + "," +
                                room.isOccupied() + "," +
                                room.isUnderMaintenance() + "," +
                                (room.getCheckInDate() != null ? room.getCheckInDate() : "") + "," +
                                (room.getCheckOutDate() != null ? room.getCheckOutDate() : "") + "," +
                                guestIds + "\n"
                );
            }
        }
    }
}
