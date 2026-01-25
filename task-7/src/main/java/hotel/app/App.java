package task_11.app;

import di.DIContainer;
import task_11.controller.GuestController;
import task_11.controller.RoomController;
import task_11.controller.ServiceController;
import task_11.controller.interfaces.IGuestController;
import task_11.controller.interfaces.IRoomController;
import task_11.controller.interfaces.IServiceController;
import task_11.db.ConnectionProvider;
import task_11.db.TransactionManager;
import task_11.db.dao.jdbc.*;
import task_11.db.interfaces.*;
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

        container.register(TransactionManager.class, TransactionManager.class);
        container.register(ConnectionProvider.class, ConnectionProvider.class);

        container.register(IGuestManager.class, GuestManager.class);
        container.register(IRoomManager.class, RoomManager.class);
        container.register(IServiceManager.class, ServiceManager.class);

        container.register(IGuestController.class, GuestController.class);
        container.register(IRoomController.class, RoomController.class);
        container.register(IServiceController.class, ServiceController.class);

        container.register(GuestRepository.class, JdbcGuestDao.class);
        container.register(RoomRepository.class, JdbcRoomDao.class);
        container.register(ServiceRepository.class, JdbcServiceDao.class);
        container.register(GuestServiceRepository.class, JdbcGuestServiceDao.class);
        container.register(StayHistoryRepository.class, JdbcStayHistoryDao.class);

        container.register(ConsoleView.class, ConsoleView.class);
        container.register(ConsoleUI.class, ConsoleUI.class);

        try {
            ConsoleUI ui = container.getBean(ConsoleUI.class);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                System.out.println("Закрытие соединения с базой данных...");
                ConnectionProvider.getInstance().close();
                System.out.println("Приложение завершено корректно.");
            }));

            ui.run();
        } catch (RuntimeException e) {
            System.err.println("Критическая ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}