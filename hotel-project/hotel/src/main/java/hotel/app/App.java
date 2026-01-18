package hotel.app;

import di.DIContainer;
import hotel.controller.GuestController;
import hotel.controller.RoomController;
import hotel.controller.ServiceController;
import hotel.controller.interfaces.IGuestController;
import hotel.controller.interfaces.IRoomController;
import hotel.controller.interfaces.IServiceController;
import hotel.db.ConnectionProvider;
import hotel.db.TransactionManager;
import hotel.db.dao.jdbc.JdbcGuestDao;
import hotel.db.dao.jdbc.JdbcGuestServiceDao;
import hotel.db.dao.jdbc.JdbcRoomDao;
import hotel.db.dao.jdbc.JdbcServiceDao;
import hotel.db.dao.jdbc.JdbcStayHistoryDao;
import hotel.db.interfaces.GuestRepository;
import hotel.db.interfaces.GuestServiceRepository;
import hotel.db.interfaces.RoomRepository;
import hotel.db.interfaces.ServiceRepository;
import hotel.db.interfaces.StayHistoryRepository;
import hotel.service.GuestManager;
import hotel.service.RoomManager;
import hotel.service.ServiceManager;
import hotel.service.interfaces.IGuestManager;
import hotel.service.interfaces.IRoomManager;
import hotel.service.interfaces.IServiceManager;
import hotel.ui.ConsoleUI;
import hotel.view.ConsoleView;

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