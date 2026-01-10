package task_11.app;

import di.DIContainer;
import task_11.controller.GuestController;
import task_11.controller.RoomController;
import task_11.controller.ServiceController;
import task_11.controller.interfaces.IGuestController;
import task_11.controller.interfaces.IRoomController;
import task_11.controller.interfaces.IServiceController;
import task_11.serialize.StateManager;
import task_11.repository.InMemoryGuestRepository;
import task_11.repository.InMemoryRoomRepository;
import task_11.repository.InMemoryServiceRepository;
import task_11.repository.interfaces.GuestRepository;
import task_11.repository.interfaces.RoomRepository;
import task_11.repository.interfaces.ServiceRepository;
import task_11.service.GuestManager;
import task_11.service.RoomManager;
import task_11.service.ServiceManager;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;
import task_11.ui.ConsoleUI;
import task_11.view.ConsoleView;

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

        ui.run();
    }
}