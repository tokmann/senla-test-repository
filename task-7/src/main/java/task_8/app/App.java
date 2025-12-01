package task_8.app;

import di.DIContainer;
import task_8.controller.GuestController;
import task_8.controller.RoomController;
import task_8.controller.ServiceController;
import task_8.controller.interfaces.IGuestController;
import task_8.controller.interfaces.IRoomController;
import task_8.controller.interfaces.IServiceController;
import task_8.serialize.StateManager;
import task_8.repository.InMemoryGuestRepository;
import task_8.repository.InMemoryRoomRepository;
import task_8.repository.InMemoryServiceRepository;
import task_8.repository.interfaces.GuestRepository;
import task_8.repository.interfaces.RoomRepository;
import task_8.repository.interfaces.ServiceRepository;
import task_8.service.GuestManager;
import task_8.service.RoomManager;
import task_8.service.ServiceManager;
import task_8.service.interfaces.IGuestManager;
import task_8.service.interfaces.IRoomManager;
import task_8.service.interfaces.IServiceManager;
import task_8.ui.ConsoleUI;
import task_8.view.ConsoleView;
import task_8.view.factory.UIFactory;
import task_8.view.factory.UIFactoryProvider;

/**
 * Главный класс консольного приложения отеля.
 * Инициализирует все компоненты системы и запускает пользовательский интерфейс.
 */
public class App {

    /**
     * Точка входа в приложение.
     */
    public static void main(String[] args) {
        DIContainer container = new DIContainer();

        container.register(IGuestManager.class, GuestManager.class);
        container.register(IRoomManager.class, RoomManager.class);
        container.register(IServiceManager.class, ServiceManager.class);
        container.register(IGuestController.class, GuestController.class);
        container.register(IRoomController.class, RoomController.class);
        container.register(IServiceController.class, ServiceController.class);
        container.register(GuestRepository.class, InMemoryGuestRepository.class);
        container.register(RoomRepository.class, InMemoryRoomRepository.class);
        container.register(ServiceRepository.class, InMemoryServiceRepository.class);
        container.register(ConsoleView.class, ConsoleView.class);

        ConsoleUI ui = container.getBean(ConsoleUI.class);

        StateManager stateManager = container.getBean(StateManager.class);

        stateManager.loadState();
        Runtime.getRuntime().addShutdownHook(new Thread(stateManager::saveState));

        ui.run();
    }
}