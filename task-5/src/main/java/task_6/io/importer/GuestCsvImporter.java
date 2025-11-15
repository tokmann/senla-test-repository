package task_6.io.importer;

import task_6.controller.GuestController;
import task_6.io.interfaces.CsvImporter;
import task_6.model.Guest;
import task_6.model.Room;
import task_6.model.Service;
import task_6.service.GuestManager;
import task_6.service.RoomManager;
import task_6.service.ServiceManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GuestCsvImporter implements CsvImporter {

    private final GuestController guestController;
    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;

    public GuestCsvImporter(GuestController guestController,
                            GuestManager guestManager,
                            RoomManager roomManager,
                            ServiceManager serviceManager) {
        this.guestController = guestController;
        this.guestManager = guestManager;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
    }

    @Override
    public void importFromCsv(String filePath) throws IOException {
        List<GuestImportData> importDataList = readCsvData(filePath);
        Map<String, Guest> createdGuests = createOrUpdateGuestsWithServices(importDataList);
        checkInGuestsToRooms(importDataList, createdGuests);
    }

    /**
     * Чтение данных из CSV файла
     */
    private List<GuestImportData> readCsvData(String filePath) throws IOException {
        List<GuestImportData> importDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                GuestImportData data = parseCsvLine(line);
                importDataList.add(data);
            }
        }

        return importDataList;
    }

    /**
     * Парсинг одной строки CSV
     */
    private GuestImportData parseCsvLine(String line) {
        String[] parts = line.split(",", -1);

        long id = Long.parseLong(parts[0]);
        int age = Integer.parseInt(parts[1]);
        String firstName = parts[2];
        String secondName = parts[3];
        long roomId = (parts.length > 4 && !parts[4].isBlank()) ? Long.parseLong(parts[4]) : -1;
        List<Long> serviceIds = parts.length > 5 ? parseServiceIds(parts[5]) : new ArrayList<>();

        return new GuestImportData(id, age, firstName, secondName, roomId, serviceIds);
    }

    /**
     * Парсинг списка ID услуг
     */
    private List<Long> parseServiceIds(String serviceIdsStr) {
        List<Long> serviceIds = new ArrayList<>();
        if (serviceIdsStr != null && !serviceIdsStr.isBlank()) {
            String[] serviceIdArray = serviceIdsStr.split(";");
            for (String sid : serviceIdArray) {
                if (!sid.isBlank()) {
                    serviceIds.add(Long.parseLong(sid));
                }
            }
        }
        return serviceIds;
    }

    /**
     * Создание гостей и добавление услуг
     */
    private Map<String, Guest> createOrUpdateGuestsWithServices(List<GuestImportData> importDataList) {
        Map<String, Guest> processedGuests = new HashMap<>();

        Map<Long, Guest> existingGuestsById = guestManager.getAllGuests().stream()
                .collect(Collectors.toMap(Guest::getId, g -> g));

        for (GuestImportData data : importDataList) {
            String fullName = data.firstName() + " " + data.secondName();
            Guest guest = existingGuestsById.get(data.id());
            if (guest != null) {
                String oldGuest = guest.getFullName();
                guest = updateExistingGuest(guest, data);
                System.out.println("Обновлен гость: " + oldGuest + " (ID: " + data.id() + ") на " + data.firstName() + " " + data.secondName());
            } else {
                guest = createNewGuest(data);
                System.out.println("Зарегистрирован гость: " + guest.getFullName() + " (ID: " + data.id() + ")");
            }
            processedGuests.put(fullName, guest);
            updateGuestServices(guest, data.serviceIds());
        }

        return processedGuests;
    }

    /**
     * Обновление существующего гостя
     */
    private Guest updateExistingGuest(Guest guest, GuestImportData data) {
        if (guest.getGuestRoom() != null) {
            guestController.checkOutGuest(guest.getId());
        }
        guestManager.removeGuest(guest);
        Guest newGuest = new Guest(0, data.age(), data.firstName(), data.secondName(), null, new ArrayList<>());
        return guestController.registerGuest(newGuest);
    }

    /**
     * Создание нового гостя
     */
    private Guest createNewGuest(GuestImportData data) {
        Guest guest = new Guest(0, data.age(), data.firstName(), data.secondName(), null, new ArrayList<>());
        return guestController.registerGuest(guest);
    }



    /**
     * Обновление услуг гостя
     */
    private void updateGuestServices(Guest guest, List<Long> serviceIds) {
        guest.getGuestServices().clear();
        for (Long serviceId : serviceIds) {
            Service service = serviceManager.getServiceById(serviceId);
            if (service != null) {
                guestManager.addServiceToGuest(guest, service);
                System.out.println("  Добавлена услуга '" + service.getName() + "' гостю " + guest.getFullName());
            } else {
                System.out.println("  Услуга с ID " + serviceId + " не найдена для гостя " + guest.getFullName());
            }
        }
    }

    /**
     * Заселение гостей в комнаты
     */
    private void checkInGuestsToRooms(List<GuestImportData> importDataList, Map<String, Guest> createdGuests) {
        for (GuestImportData data : importDataList) {
            if (data.roomId() != -1) {
                checkInGuest(data, createdGuests);
            }
        }
    }

    /**
     * Заселение одного гостя в комнату
     */
    private void checkInGuest(GuestImportData data, Map<String, Guest> createdGuests) {
        String fullName = data.firstName() + " " + data.secondName();
        Guest guest = createdGuests.get(fullName);

        if (guest == null) {
            System.out.println("Гость " + fullName + " не найден для заселения");
            return;
        }

        Optional<Room> roomOpt = roomManager.findRoomById(data.roomId());
        if (roomOpt.isEmpty()) {
            System.out.println("Комната с ID " + data.roomId() + " не найдена для гостя " + fullName);
            return;
        }

        Room room = roomOpt.get();
        boolean success = guestController.checkInGuest(
                guest.getId(),
                room.getNumber(),
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        if (success) {
            System.out.println("Гость " + fullName + " заселен в комнату " + room.getNumber());
        } else {
            System.out.println("Не удалось заселить гостя " + fullName + " в комнату " + room.getNumber() +
                    " (комната занята или недостаточно мест)");
        }
    }

    /**
     * Вспомогательный класс для хранения данных импорта
     */
    private record GuestImportData(
            long id,
            int age,
            String firstName,
            String secondName,
            long roomId,
            List<Long> serviceIds
    ) {}
}
