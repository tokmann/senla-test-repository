package task_6.app;

import task_6.controller.GuestController;
import task_6.controller.ImportExportController;
import task_6.controller.RoomController;
import task_6.controller.ServiceController;
import task_6.io.exporter.GuestCsvExporter;
import task_6.io.exporter.RoomCsvExporter;
import task_6.io.exporter.ServiceCsvExporter;
import task_6.io.importer.GuestCsvImporter;
import task_6.io.importer.RoomCsvImporter;
import task_6.io.importer.ServiceCsvImporter;
import task_6.repository.impl.InMemoryGuestRepository;
import task_6.repository.impl.InMemoryRoomRepository;
import task_6.repository.impl.InMemoryServiceRepository;
import task_6.repository.interfaces.GuestRepository;
import task_6.repository.interfaces.RoomRepository;
import task_6.repository.interfaces.ServiceRepository;
import task_6.service.GuestManager;
import task_6.service.RoomManager;
import task_6.service.ServiceManager;
import task_6.ui.ConsoleUI;
import task_6.view.ConsoleView;
import task_6.view.factory.UIFactory;
import task_6.view.factory.UIFactoryProvider;

/**
 * Главный класс консольного приложения отеля.
 * Инициализирует все компоненты системы и запускает пользовательский интерфейс.
 */
public class App {

    /**
     * Точка входа в приложение. Инициализирует все слои приложения:
     */
    public static void main(String[] args) {
        // Фабрика для создания UI (поддерживает возможность замены интерфейса)
        UIFactory factory = UIFactoryProvider.getInstance();
        ConsoleView consoleView = factory.createConsoleView();

        // Инициализация менеджеров и репозиториев (в будущем можно заменить на БД)
        GuestRepository inMemoryGuestRepository = new InMemoryGuestRepository();
        RoomRepository inMemoryRoomRepository = new InMemoryRoomRepository();
        ServiceRepository inMemoryServiceRepository = new InMemoryServiceRepository();
        GuestManager guestManager = new GuestManager(inMemoryGuestRepository);
        RoomManager roomManager = new RoomManager(inMemoryRoomRepository, guestManager);
        ServiceManager serviceManager = new ServiceManager(inMemoryServiceRepository);

        // Контроллеры связывают View и бизнес-логику
        GuestController guestController = new GuestController(guestManager, roomManager, serviceManager);
        RoomController roomController = new RoomController(roomManager);
        ServiceController serviceController = new ServiceController(serviceManager);

        // Импорт/экспорт
        GuestCsvExporter guestCsvExporter = new GuestCsvExporter(guestManager);
        GuestCsvImporter guestCsvImporter = new GuestCsvImporter(guestController, guestManager,
                roomManager, serviceManager);
        RoomCsvExporter roomCsvExporter = new RoomCsvExporter(roomManager);
        RoomCsvImporter roomCsvImporter = new RoomCsvImporter(roomManager, guestManager);
        ServiceCsvExporter serviceCsvExporter = new ServiceCsvExporter(serviceManager, guestManager);
        ServiceCsvImporter serviceCsvImporter = new ServiceCsvImporter(serviceManager, guestManager);

        ImportExportController importExportController = new ImportExportController(guestCsvImporter, guestCsvExporter,
                roomCsvImporter, roomCsvExporter,
                serviceCsvImporter, serviceCsvExporter);

        ConsoleUI ui = new ConsoleUI(consoleView, guestController,
                roomController, serviceController,
                importExportController, guestManager,
                roomManager, serviceManager);

        ui.run();
    }
}