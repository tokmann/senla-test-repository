package task_7.app;

import task_7.controller.GuestController;
import task_7.controller.RoomController;
import task_7.controller.ServiceController;
import task_7.controller.interfaces.IGuestController;
import task_7.controller.interfaces.IRoomController;
import task_7.controller.interfaces.IServiceController;
import task_7.serialize.StateManager;
import task_7.repository.InMemoryGuestRepository;
import task_7.repository.InMemoryRoomRepository;
import task_7.repository.InMemoryServiceRepository;
import task_7.repository.interfaces.GuestRepository;
import task_7.repository.interfaces.RoomRepository;
import task_7.repository.interfaces.ServiceRepository;
import task_7.service.GuestManager;
import task_7.service.RoomManager;
import task_7.service.ServiceManager;
import task_7.service.interfaces.IGuestManager;
import task_7.service.interfaces.IRoomManager;
import task_7.service.interfaces.IServiceManager;
import task_7.ui.ConsoleUI;
import task_7.view.ConsoleView;
import task_7.view.factory.UIFactory;
import task_7.view.factory.UIFactoryProvider;

/**
 * Главный класс консольного приложения отеля.
 * Инициализирует все компоненты системы и запускает пользовательский интерфейс.
 */
public class App {

    /**
     * Точка входа в приложение. Инициализирует все слои приложения:
     */
    public static void main(String[] args) {
        // Фабрика для создания UI
        UIFactory factory = UIFactoryProvider.getInstance();
        ConsoleView consoleView = factory.createConsoleView();

        // Инициализация менеджеров и репозиториев
        GuestRepository inMemoryGuestRepository = new InMemoryGuestRepository();
        RoomRepository inMemoryRoomRepository = new InMemoryRoomRepository();
        ServiceRepository inMemoryServiceRepository = new InMemoryServiceRepository();
        IGuestManager guestManager = new GuestManager(inMemoryGuestRepository);
        IRoomManager roomManager = new RoomManager(inMemoryRoomRepository, guestManager);
        IServiceManager serviceManager = new ServiceManager(inMemoryServiceRepository);

        // Менеджер состояния для сериализации
        StateManager stateManager = new StateManager(guestManager, roomManager, serviceManager);
        stateManager.loadState();

        // Контроллеры
        IGuestController guestController = new GuestController(guestManager, roomManager, serviceManager);
        IRoomController roomController = new RoomController(roomManager);
        IServiceController serviceController = new ServiceController(serviceManager);

        ConsoleUI ui = new ConsoleUI(consoleView, guestController,
                roomController, serviceController, guestManager,
                roomManager, serviceManager);

        // Shutdown hook для сохранения состояния при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(stateManager::saveState));

        ui.run();
    }
}