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

/**
 * Импортер данных гостей из CSV формата.
 * Обрабатывает файлы с информацией о гостях, их комнатах и услугах.
 * Поддерживает создание новых гостей и обновление существующих по ID.
 * Восстанавливает связи гостей с комнатами и услугами.
 */
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

    /**
     * Импортирует гостей из CSV файла.
     * Формат файла: id,age,firstName,secondName,roomId,serviceIds
     * @param filePath путь к CSV файлу
     */
    @Override
    public void importFromCsv(String filePath) throws IOException {
        List<GuestImportData> importDataList = readCsvData(filePath);
        Map<String, Guest> createdGuests = createOrUpdateGuestsWithServices(importDataList);
        checkInGuestsToRooms(importDataList, createdGuests);
    }

    /**
     * Читает и парсит данные из CSV файла.
     * @param filePath путь к CSV файлу
     * @return список данных для импорта гостей
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
     * Парсит одну строку CSV в объект GuestImportData.
     * Обрабатывает опциональные поля roomId и serviceIds.
     * @param line строка CSV файла
     * @return данные гостя для импорта
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
     * Парсит строку с ID услуг в список Long.
     * Формат строки: "1;2;3"
     * @param serviceIdsStr строка с ID услуг через точку с запятой
     * @return список ID услуг
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
     * Создает или обновляет гостей и добавляет им услуги.
     * Если гость с таким ID уже существует - он обновляется.
     * Если нет - создается новый гость.
     * @param importDataList список данных для импорта
     * @return мапа созданных/обновленных гостей (ключ - полное имя)
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
     * Обновляет существующего гостя новыми данными.
     * Выселяет гостя из текущей комнаты перед обновлением.
     * @param guest существующий гость
     * @param data новые данные из CSV
     * @return обновленный гость
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
     * Создает нового гостя на основе данных из CSV.
     * @param data данные гостя из CSV
     * @return созданный гость
     */
    private Guest createNewGuest(GuestImportData data) {
        Guest guest = new Guest(0, data.age(), data.firstName(), data.secondName(), null, new ArrayList<>());
        return guestController.registerGuest(guest);
    }



    /**
     * Обновляет список услуг гостя.
     * Очищает существующие услуги и добавляет новые из CSV.
     * @param guest гость для обновления
     * @param serviceIds список ID услуг из CSV
     */
    private void updateGuestServices(Guest guest, List<Long> serviceIds) {
        guest.getGuestServices().clear();
        for (Long serviceId : serviceIds) {
            Optional<Service> serviceOpt = serviceManager.getServiceById(serviceId);
            if (serviceOpt.isPresent()) {
                Service service = serviceOpt.get();
                guestManager.addServiceToGuest(guest, service);
                System.out.println("  Добавлена услуга '" + service.getName() + "' гостю " + guest.getFullName());
            } else {
                System.out.println("  Услуга с ID " + serviceId + " не найдена для гостя " + guest.getFullName());
            }
        }
    }

    /**
     * Заселяет гостей в комнаты на основе данных из CSV.
     * @param importDataList список данных импорта
     * @param createdGuests карта созданных гостей
     */
    private void checkInGuestsToRooms(List<GuestImportData> importDataList, Map<String, Guest> createdGuests) {
        for (GuestImportData data : importDataList) {
            if (data.roomId() != -1) {
                checkInGuest(data, createdGuests);
            }
        }
    }

    /**
     * Заселяет одного гостя в комнату.
     * Ищет комнату по ID и гостя по имени.
     * @param data данные гостя из CSV
     * @param createdGuests карта созданных гостей
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
     * Вспомогательная запись для хранения данных импорта гостя.
     * Содержит все поля из CSV файла в структурированном виде.
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
