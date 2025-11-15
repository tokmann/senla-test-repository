package task_6.io.importer;

import task_6.model.Guest;
import task_6.model.Room;
import task_6.service.GuestManager;
import task_6.service.RoomManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomCsvImporter {

    private final RoomManager roomManager;
    private final GuestManager guestManager;

    public RoomCsvImporter(RoomManager roomManager, GuestManager guestManager) {
        this.roomManager = roomManager;
        this.guestManager = guestManager;
    }

    public void importFromCsv(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);

                long id = Long.parseLong(p[0]);
                int number = Integer.parseInt(p[1]);
                int capacity = Integer.parseInt(p[2]);
                double price = Double.parseDouble(p[3]);
                int stars = Integer.parseInt(p[4]);
                boolean isOccupied = Boolean.parseBoolean(p[5]);
                boolean underMaintenance = Boolean.parseBoolean(p[6]);
                LocalDate checkInDate = (p.length > 7 && !p[7].isBlank()) ? LocalDate.parse(p[7]) : null;
                LocalDate checkOutDate = (p.length > 8 && !p[8].isBlank()) ? LocalDate.parse(p[8]) : null;
                List<Long> guestIds = p.length > 9 ? parseGuestIds(p[9]) : new ArrayList<>();

                Room room = new Room(id, number, capacity, price, stars);

                if (underMaintenance && !isOccupied) {
                    room.setMaintenance(true);
                }

                boolean added = roomManager.addRoom(room);
                if (added) {
                    System.out.println("Комната добавлена/обновлена: " + number + " (ID: " + id + ")");
                    if (!guestIds.isEmpty() && checkInDate != null && checkOutDate != null) {
                        restoreRoomOccupancy(room, guestIds, checkInDate, checkOutDate);
                    }
                } else {
                    System.out.println("Комната с номером " + number + " уже существует");
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

    /**
     * Восстанавливаем заселение гостей в комнату
     */
    private void restoreRoomOccupancy(Room room, List<Long> guestIds, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Guest> guestsToCheckIn = new ArrayList<>();
        for (Long guestId : guestIds) {
            Optional<Guest> guestOpt = guestManager.getGuestById(guestId);
            if (guestOpt.isPresent()) {
                Guest guest = guestOpt.get();
                guestsToCheckIn.add(guest);
                guest.setGuestRoom(room);
            } else {
                System.out.println("Гость с ID " + guestId + " не найден для комнаты " + room.getNumber());
            }
        }

        if (!guestsToCheckIn.isEmpty()) {
            boolean success = room.checkIn(guestsToCheckIn, checkInDate, checkOutDate);
            if (success) {
                System.out.println("В комнату " + room.getNumber() + " заселено " + guestsToCheckIn.size() + " гостей");
            } else {
                System.out.println("Ошибка заселения гостей в комнату " + room.getNumber());
            }
        }
    }
}
