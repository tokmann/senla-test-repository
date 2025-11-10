package task_5.app;

import task_5.controller.GuestController;
import task_5.controller.RoomController;
import task_5.controller.ServiceController;
import task_5.model.Guest;
import task_5.model.Room;
import task_5.model.Service;
import task_5.model.repository.GuestRepository;
import task_5.model.repository.RoomRepository;
import task_5.model.repository.ServiceRepository;
import task_5.service.GuestManager;
import task_5.service.RoomManager;
import task_5.service.ServiceManager;
import task_5.view.ConsoleView;
import task_5.view.enums.GuestSortOption;
import task_5.view.enums.RoomSortOption;
import task_5.view.enums.ServiceSortOption;
import task_5.view.factory.UIFactory;
import task_5.view.factory.UIFactoryProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Главный класс консольного приложения отеля.
 * Класс служит точкой входа и управляет основным циклом пользовательского интерфейса.
 */
public class ConsoleHotelApplication {

    public static void main(String[] args) {
        // Фабрика для создания UI (поддерживает возможность замены интерфейса)
        UIFactory factory = UIFactoryProvider.getInstance();
        ConsoleView consoleView = factory.createConsoleView();

        // Инициализация менеджеров и репозиториев (в будущем можно заменить на БД)
        GuestManager guestManager = new GuestManager(new GuestRepository());
        RoomManager roomManager = new RoomManager(new RoomRepository(), guestManager);
        ServiceManager serviceManager = new ServiceManager(new ServiceRepository());

        // Контроллеры связывают View и бизнес-логику
        GuestController guestController = new GuestController(guestManager, roomManager, serviceManager);
        RoomController roomController = new RoomController(roomManager, consoleView);
        ServiceController serviceController = new ServiceController(serviceManager);

        new ConsoleHotelApplication().run(consoleView, guestController, roomController, serviceController, guestManager, roomManager, serviceManager);
    }

    private void run(ConsoleView consoleView,
                     GuestController guestController,
                     RoomController roomController,
                     ServiceController serviceController,
                     GuestManager guestManager,
                     RoomManager roomManager,
                     ServiceManager serviceManager) {

        Scanner scanner = new Scanner(System.in);
        consoleView.printWelcome();

        boolean running = true;
        while (running) {
            consoleView.println("""
                    
                    ===== Главное меню =====
                    1 — Показать все номера
                    2 — Показать свободные номера
                    3 — Зарегистрировать гостя
                    4 — Показать всех гостей
                    5 — Добавить услугу
                    6 — Показать все услуги
                    7 — Узнать количество свободных номеров
                    8 — Узнать количество гостей
                    9 — Найти номера, которые будут свободны к дате
                    10 — Рассчитать полную оплату за номер
                    11 — Показать историю последних гостей номера
                    12 — Показать услуги гостя
                    13 — Показать подробности номера
                    14 — Выселить гостей из комнаты
                    15 - Добавить новую комнату
                    16 - Добавить гостю существующую услугу
                    0 — Выход
                    """);

            consoleView.print("Выберите действие: ");
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> showAllRooms(consoleView, roomController, scanner);                     // Показать все номера
                    case "2" -> showFreeRooms(consoleView, roomController, scanner);                   // Показать свободные номера
                    case "3" -> registerGuest(consoleView, guestController, scanner);                  // Регистрация гостя
                    case "4" -> showAllGuests(consoleView, guestController, scanner);                  // Список всех гостей
                    case "5" -> addService(consoleView, serviceController, scanner);                   // Добавление услуги
                    case "6" -> showAllServices(consoleView, serviceController, scanner);              // Показать все услуги
                    case "7" -> consoleView.println("Количество свободных номеров: " + roomController.countFreeRooms());
                    case "8" -> consoleView.println("Количество гостей: " + guestController.countGuests());
                    case "9" -> findRoomsFreeByDate(consoleView, roomController, scanner);             // Свободные номера к дате
                    case "10" -> calculateFullRoomPrice(consoleView, roomController, scanner);         // Расчет стоимости
                    case "11" -> showRoomHistory(consoleView, roomController, scanner);                // История гостей
                    case "12" -> showGuestServices(consoleView, guestController, scanner);             // Просмотр услуг гостя
                    case "13" -> showRoomDetails(consoleView, roomController, scanner);                // Подробности по номеру
                    case "14" -> checkOutGuests(consoleView, roomManager, scanner);                    // Выселение гостей
                    case "15" -> addNewRoom(consoleView, roomController, scanner);                     // Добавление комнаты
                    case "16" -> addServiceToGuest(consoleView, guestManager, guestController, serviceManager, scanner); // Добавление услуги гостю
                    case "0" -> {
                        running = false;
                        consoleView.printGoodbye();
                    }
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                consoleView.println("Ошибка: " + e.getMessage());
            }
        }
    }

    // 1 — Показать все номера
    private void showAllRooms(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = controller.getAllRooms(option);
        rooms.forEach(view::println);
    }

    // 2 — Показать свободные номера
    private void showFreeRooms(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = controller.getFreeRooms(option);
        rooms.forEach(view::println);
    }

    // 3 — Регистрация гостя
    private void registerGuest(ConsoleView view, GuestController controller, Scanner scanner) {
        view.print("Имя: ");
        String firstName = scanner.nextLine();
        view.print("Фамилия: ");
        String lastName = scanner.nextLine();
        view.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        view.print("Номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());

        Guest guest = controller.registerGuest(firstName, lastName, age, roomNumber,
                LocalDate.now(), LocalDate.now().plusDays(3));

        if (guest != null) view.println("Гость зарегистрирован: " + guest);
        else view.println("Ошибка: комната не найдена или занята. Регистрация не прошла.");
    }

    // 4 — Список всех гостей
    private void showAllGuests(ConsoleView view, GuestController controller, Scanner scanner) {
        view.print("Критерий сортировки (Алфавит, Дата освобождения номера): ");
        String sortInput = scanner.nextLine();
        GuestSortOption option = GuestSortOption.fromDescription(sortInput);
        List<Guest> guests = controller.getSortedGuests(option);
        guests.forEach(view::println);
    }

    // 5 — Добавление услуги
    private void addService(ConsoleView view, ServiceController controller, Scanner scanner) {
        view.print("Название услуги: ");
        String name = scanner.nextLine();
        view.print("Описание: ");
        String desc = scanner.nextLine();
        view.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());
        Service service = controller.addService(name, desc, price, LocalDate.now());
        view.println("Услуга добавлена: " + service);
    }

    // 6 — Показать все услуги
    private void showAllServices(ConsoleView view, ServiceController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Название): ");
        String sortInput = scanner.nextLine();
        ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
        List<Service> services = controller.getServices(option);
        services.forEach(view::println);
    }

    // 9 — Свободные номера к определенной дате
    private void findRoomsFreeByDate(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        List<Room> rooms = controller.findRoomsThatWillBeFree(date);
        view.println("Номера, которые будут свободны к " + date + ":");
        rooms.forEach(view::println);
    }

    // 10 — Расчет стоимости
    private void calculateFullRoomPrice(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Double> price = controller.getFullRoomPrice(roomNumber);
        price.ifPresentOrElse(
                p -> view.println("Полная оплата за номер: " + p),
                () -> view.println("Номер не найден")
        );
    }

    // 11 — История гостей
    private void showRoomHistory(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        view.print("Введите количество последних гостей для истории: ");
        int historyLength = Integer.parseInt(scanner.nextLine());
        List<String> history = controller.getRoomHistory(roomNumber, historyLength);
        history.forEach(view::println);
    }

    // 12 — Просмотр услуг гостя
    private void showGuestServices(ConsoleView view, GuestController controller, Scanner scanner) {
        view.print("Введите имя и фамилию гостя: ");
        String guestName = scanner.nextLine();
        Guest guest = controller.findGuestByFullName(guestName);
        if (guest != null) {
            view.print("Критерий сортировки услуг (Цена, Название): ");
            String sortInput = scanner.nextLine();
            ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
            List<Service> services = controller.getGuestServices(guest, option);
            services.forEach(view::println);
        } else view.println("Гость не найден");
    }

    // 13 — Подробности по номеру
    private void showRoomDetails(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Room> room = controller.getFullRoomInfo(roomNumber);
        room.ifPresentOrElse(view::println, () -> view.println("Номер не найден"));
    }

    // 14 — Выселение гостей
    private void checkOutGuests(ConsoleView view, RoomManager roomManager, Scanner scanner) {
        view.print("Введите номер комнаты для выселения: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        boolean success = roomManager.checkOut(roomNumber);
        if (success) view.println("Гости из комнаты " + roomNumber + " успешно выселены.");
        else view.println("Ошибка: комната не найдена или пуста.");
    }

    // 15 — Добавление комнаты
    private void addNewRoom(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Номер комнаты: ");
        int number = Integer.parseInt(scanner.nextLine());
        view.print("Вместимость: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        view.print("Звезды: ");
        int stars = Integer.parseInt(scanner.nextLine());
        view.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());

        boolean added = controller.addRoom(number, capacity, stars, price);
        if (added) view.println("Номер добавлен успешно");
        else view.println("Номер с таким номером уже существует");
    }

    // 16 — Добавление услуги гостю
    private void addServiceToGuest(ConsoleView view, GuestManager guestManager, GuestController guestController,
                                   ServiceManager serviceManager, Scanner scanner) {

        view.print("Введите имя и фамилию гостя: ");
        String guestName = scanner.nextLine();
        Guest guest = guestManager.findGuestByFullName(guestName);

        if (guest == null) {
            view.println("Гость не найден.");
            return;
        }

        List<Service> services = serviceManager.getSortedServices(ServiceSortOption.NAME);
        view.println("Доступные услуги:");
        services.forEach(s -> view.println("- " + s.getName() + " (Цена: " + s.getPrice() + ")"));

        view.print("Введите название услуги: ");
        String serviceName = scanner.nextLine();

        boolean ok = guestController.addServiceToGuestByName(guestName, serviceName);
        if (ok) view.println("Услуга добавлена гостю.");
        else view.println("Услуга не найдена.");
    }
}