package task_6.ui;

import task_6.controller.GuestController;
import task_6.controller.ImportExportController;
import task_6.controller.RoomController;
import task_6.controller.ServiceController;
import task_6.model.Guest;
import task_6.model.Room;
import task_6.model.Service;
import task_6.service.GuestManager;
import task_6.service.RoomManager;
import task_6.service.ServiceManager;
import task_6.view.ConsoleView;
import task_6.view.enums.GuestSortOption;
import task_6.view.enums.RoomSortOption;
import task_6.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Основной пользовательский интерфейс консольного приложения отеля.
 * Координирует взаимодействие между пользователем и системой через консоль.
 */
public class ConsoleUI {

    private final ConsoleView consoleView;
    private final GuestController guestController;
    private final RoomController roomController;
    private final ServiceController serviceController;
    private final ImportExportController importExportController;
    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;

    public ConsoleUI(ConsoleView consoleView,
                     GuestController guestController,
                     RoomController roomController,
                     ServiceController serviceController,
                     ImportExportController importExportController,
                     GuestManager guestManager,
                     RoomManager roomManager,
                     ServiceManager serviceManager) {
        this.consoleView = consoleView;
        this.guestController = guestController;
        this.roomController = roomController;
        this.serviceController = serviceController;
        this.importExportController = importExportController;
        this.guestManager = guestManager;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
    }

    /**
     * Запускает главный цикл приложения.
     * Отображает приветственное сообщение и переходит в главное меню.
     */
    public void run() {
        consoleView.printWelcome();
        mainMenu();
    }

    /**
     * Главное меню приложения.
     * Предоставляет доступ к основным модулям системы.
     * Обрабатывает навигацию между разделами и выход из приложения.
     */
    private void mainMenu() {

        Scanner in = new Scanner(System.in);
        boolean running = true;

        while (running) {
            consoleView.println("""
                    
                    ===== Главное меню =====
                    1 — Гости
                    2 — Комнаты
                    3 — Услуги
                    4 — Импорт / Экспорт
                    0 — Выход
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            switch (input) {
                case "1" -> guestMenu();
                case "2" -> roomMenu();
                case "3" -> serviceMenu();
                case "4" -> importExportMenu();
                case "0" -> {
                    running = false;
                    consoleView.printGoodbye();
                }
                default -> consoleView.printInvalidOption();
            }
        }
    }

    /**
     * Меню управления гостями.
     * Предоставляет операции регистрации, заселения, выселения и управления услугами гостей.
     */
    private void guestMenu() {

        Scanner in = new Scanner(System.in);
        boolean back = false;

        while (!back) {
            consoleView.println("""
                
                ===== Меню гостей =====
                1 — Зарегистрировать гостя
                2 — Показать всех гостей
                3 — Показать незаселенных гостей
                4 — Показать заселенных гостей
                5 — Заселить гостя в комнату
                6 — Выселить гостя из комнаты
                7 — Показать услуги гостя
                8 — Добавить услугу гостю
                9 — Количество гостей
                0 — Назад
                """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> registerGuestOnly(consoleView, guestController, in);
                    case "2" -> showAllGuests(consoleView, guestController, in);
                    case "3" -> showGuestsNotCheckedIn(consoleView, guestController, in);
                    case "4" -> showGuestsCheckedIn(consoleView, guestController, in);
                    case "5" -> checkInGuestToRoom(consoleView, guestController, in);
                    case "6" -> checkOutGuestFromRoom(consoleView, guestController, in);
                    case "7" -> showGuestServices(consoleView, guestController, in);
                    case "8" -> addServiceToGuest(consoleView, guestManager, guestController, serviceManager, in);
                    case "9" -> consoleView.println("Количество гостей: " + guestController.countGuests());
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                consoleView.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Меню управления комнатами.
     * Предоставляет операции просмотра, добавления, управления состоянием комнат.
     */
    private void roomMenu() {

        Scanner in = new Scanner(System.in);
        boolean back = false;

        while (!back) {
            consoleView.println("""
                    
                    ===== Меню комнат =====
                    1 — Показать все номера
                    2 — Показать свободные номера
                    3 — Добавить номер
                    4 — Выселить гостей
                    5 — История гостей комнаты
                    6 — Подробности комнаты
                    7 — Номера свободные к дате
                    8 — Полная стоимость проживания
                    9 — Количество свободных номеров
                    0 — Назад
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> showAllRooms(consoleView, roomController, in);
                    case "2" -> showFreeRooms(consoleView, roomController, in);
                    case "3" -> addNewRoom(consoleView, roomController, in);
                    case "4" -> checkOutGuests(consoleView, roomManager, in);
                    case "5" -> showRoomHistory(consoleView, roomController, in);
                    case "6" -> showRoomDetails(consoleView, roomController, in);
                    case "7" -> findRoomsFreeByDate(consoleView, roomController, in);
                    case "8" -> calculateFullRoomPrice(consoleView, roomController, in);
                    case "9" -> consoleView.println("Свободных номеров: " + roomController.countFreeRooms());
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                consoleView.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Меню управления услугами.
     * Предоставляет операции просмотра и добавления услуг.
     */
    private void serviceMenu() {

        Scanner in = new Scanner(System.in);
        boolean back = false;

        while (!back) {
            consoleView.println("""
                    
                    ===== Меню услуг =====
                    1 — Показать все услуги
                    2 — Добавить услугу
                    0 — Назад
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> showAllServices(consoleView, serviceController, in);
                    case "2" -> addService(consoleView, serviceController, in);
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                consoleView.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Меню импорта и экспорта данных.
     * Предоставляет операции загрузки и выгрузки данных в CSV формате.
     */
    private void importExportMenu() {

        Scanner in = new Scanner(System.in);
        boolean back = false;

        while (!back) {
            consoleView.println("""
                    
                    ===== Импорт / Экспорт =====
                    1 — Импорт гостей
                    2 — Импорт комнат
                    3 — Импорт услуг
                    4 — Экспорт гостей
                    5 — Экспорт комнат
                    6 — Экспорт услуг
                    0 — Назад
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> importGuests();
                    case "2" -> importRooms();
                    case "3" -> importServices();
                    case "4" -> exportGuests();
                    case "5" -> exportRooms();
                    case "6" -> exportServices();
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                consoleView.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Отображает все комнаты с возможностью сортировки.
     * @param view представление для вывода
     * @param controller контроллер комнат
     * @param scanner сканер для ввода пользователя
     */
    private void showAllRooms(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = controller.getAllRooms(option);
        rooms.forEach(view::println);
    }

    /**
     * Отображает свободные комнаты с возможностью сортировки.
     * @param view представление для вывода
     * @param controller контроллер комнат
     * @param scanner сканер для ввода пользователя
     */
    private void showFreeRooms(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = controller.getFreeRooms(option);
        rooms.forEach(view::println);
    }

    /**
     * Регистрирует нового гостя в системе.
     * @param view представление для ввода/вывода
     * @param controller контроллер гостей
     * @param scanner сканер для ввода данных
     */
    private void registerGuestOnly(ConsoleView view, GuestController controller, Scanner scanner) {
        view.println("\n=== Регистрация нового гостя ===");
        view.print("Имя: ");
        String firstName = scanner.nextLine();
        view.print("Фамилия: ");
        String lastName = scanner.nextLine();
        view.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        Guest guest = new Guest(0, age, firstName, lastName, null, new ArrayList<>());
        controller.registerGuest(guest);
        if (guest != null) {
            view.println("Гость зарегистрирован в системе: " + guest.getFullName() + " (ID: " + guest.getId() + ")");
        } else {
            view.println("Ошибка регистрации гостя.");
        }
    }

    /**
     * Заселяет гостя в комнату.
     * Показывает список доступных гостей и обрабатывает процесс заселения.
     * @param view представление для ввода/вывода
     * @param controller контроллер гостей
     * @param scanner сканер для ввода данных
     */
    private void checkInGuestToRoom(ConsoleView view, GuestController controller, Scanner scanner) {
        view.println("\n=== Заселение гостя в комнату ===");

        List<Guest> availableGuests = guestManager.getGuestsNotCheckedIn();
        if (availableGuests.isEmpty()) {
            view.println("Нет незаселенных гостей для заселения.");
            return;
        }

        view.println("Доступные гости для заселения:");
        for (Guest guest : availableGuests) {
            view.println("ID: " + guest.getId() + " - " + guest.getFullName() + ", возраст: " + guest.getAge());
        }

        view.print("Выберите ID гостя для заселения: ");
        int guestId = Integer.parseInt(scanner.nextLine());

        view.print("Номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());

        view.print("Дата заселения (гггг-мм-дд, сегодня - пусто): ");
        String checkInInput = scanner.nextLine();
        LocalDate checkIn = checkInInput.isEmpty() ? LocalDate.now() : LocalDate.parse(checkInInput);

        view.print("Дата выселения (гггг-мм-дд, +3 дня - пусто): ");
        String checkOutInput = scanner.nextLine();
        LocalDate checkOut = checkOutInput.isEmpty() ? checkIn.plusDays(3) : LocalDate.parse(checkOutInput);

        boolean success = controller.checkInGuest(guestId, roomNumber, checkIn, checkOut);

        if (success) {
            view.println("Гость успешно заселен в комнату " + roomNumber);
        } else {
            view.println("Ошибка: комната не найдена, занята, недостаточно мест или гость уже заселен.");
        }
    }

    /**
     * Выселяет гостя из комнаты.
     * Показывает список заселенных гостей и обрабатывает процесс выселения.
     * @param view представление для ввода/вывода
     * @param controller контроллер гостей
     * @param scanner сканер для ввода данных
     */
    private void checkOutGuestFromRoom(ConsoleView view, GuestController controller, Scanner scanner) {
        view.println("\n=== Выселение гостя из комнаты ===");

        List<Guest> checkedInGuests = guestManager.getGuestsCheckedIn();
        if (checkedInGuests.isEmpty()) {
            view.println("Нет заселенных гостей.");
            return;
        }

        view.println("Заселенные гости:");
        for (Guest guest : checkedInGuests) {
            view.println("ID: " + guest.getId() + " - " + guest.getFullName() +
                    ", комната: " + guest.getGuestRoom().getNumber());
        }

        view.print("Выберите ID гостя для выселения: ");
        int guestId = Integer.parseInt(scanner.nextLine());

        boolean success = controller.checkOutGuest(guestId);

        if (success) {
            view.println("Гость успешно выселен из комнаты.");
        } else {
            view.println("Ошибка выселения гостя.");
        }
    }

    /**
     * Отображает гостей, не заселенных в комнаты.
     * @param view представление для вывода
     * @param controller контроллер гостей
     * @param scanner сканер для ввода
     */
    private void showGuestsNotCheckedIn(ConsoleView view, GuestController controller, Scanner scanner) {
        List<Guest> guests = guestManager.getGuestsNotCheckedIn();
        if (guests.isEmpty()) {
            view.println("Нет незаселенных гостей.");
        } else {
            view.println("Незаселенные гости:");
            guests.forEach(g -> view.println(" - " + g.getFullName() + ", возраст: " + g.getAge()));
        }
    }

    /**
     * Отображает заселенных гостей.
     * @param view представление для вывода
     * @param controller контроллер гостей
     * @param scanner сканер для ввода
     */
    private void showGuestsCheckedIn(ConsoleView view, GuestController controller, Scanner scanner) {
        List<Guest> guests = guestManager.getGuestsCheckedIn();
        if (guests.isEmpty()) {
            view.println("Нет заселенных гостей.");
        } else {
            view.println("Заселенные гости:");
            guests.forEach(g -> view.println(" - " + g.getFullName() +
                    ", комната: " + g.getGuestRoom().getNumber()));
        }
    }

    /**
     * Отображает всех гостей с возможностью сортировки по различным критериям.
     * Пользователь выбирает критерий сортировки из доступных опций.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер гостей для получения отсортированного списка
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showAllGuests(ConsoleView view, GuestController controller, Scanner scanner) {
        view.print("Критерий сортировки (Алфавит, Дата освобождения номера): ");
        String sortInput = scanner.nextLine();
        GuestSortOption option = GuestSortOption.fromDescription(sortInput);
        List<Guest> guests = controller.getSortedGuests(option);
        guests.forEach(view::println);
    }

    /**
     * Добавляет новую услугу в систему.
     * Запрашивает у пользователя название, описание и цену услуги.
     * Создает услугу с текущей датой и сохраняет через контроллер.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер услуг для сохранения новой услуги
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void addService(ConsoleView view, ServiceController controller, Scanner scanner) {
        view.print("Название услуги: ");
        String name = scanner.nextLine();
        view.print("Описание: ");
        String description = scanner.nextLine();
        view.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());
        Service service = new Service(0, name, description, price, LocalDate.now());
        controller.addService(service);
        view.println("Услуга добавлена: " + service);
    }

    /**
     * Отображает все услуги с возможностью сортировки по цене или названию.
     * Пользователь выбирает критерий сортировки из доступных опций.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер услуг для получения отсортированного списка
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showAllServices(ConsoleView view, ServiceController controller, Scanner scanner) {
        view.print("Критерий сортировки (Цена, Название): ");
        String sortInput = scanner.nextLine();
        ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
        List<Service> services = controller.getServices(option);
        services.forEach(view::println);
    }

    /**
     * Находит и отображает номера, которые будут свободны к указанной дате.
     * Включает уже свободные номера и те, которые освободятся к заданной дате.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер комнат для поиска доступных номеров
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void findRoomsFreeByDate(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        List<Room> rooms = controller.findRoomsThatWillBeFree(date);
        view.println("Номера, которые будут свободны к " + date + ":");
        rooms.forEach(view::println);
    }

    /**
     * Рассчитывает и отображает полную стоимость проживания в номере.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер комнат для расчета стоимости
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void calculateFullRoomPrice(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Double> price = controller.getFullRoomPrice(roomNumber);
        price.ifPresentOrElse(
                p -> view.println("Полная оплата за номер: " + p),
                () -> view.println("Номер не найден")
        );
    }

    /**
     * Отображает историю проживания в указанной комнате.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер комнат для получения истории
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showRoomHistory(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        view.print("Введите количество последних гостей для истории: ");
        int historyLength = Integer.parseInt(scanner.nextLine());
        List<String> history = controller.getRoomHistory(roomNumber, historyLength);
        history.forEach(view::println);
    }

    /**
     * Отображает услуги конкретного гостя с возможностью сортировки.
     * Сначала находит гостя по имени, затем показывает его услуги.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер гостей для поиска гостя и его услуг
     * @param scanner сканер для чтения пользовательского ввода
     */
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

    /**
     * Отображает подробную информацию о конкретной комнате.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер комнат для получения информации о комнате
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showRoomDetails(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Room> room = controller.getFullRoomInfo(roomNumber);
        room.ifPresentOrElse(view::println, () -> view.println("Номер не найден"));
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     * @param view представление для ввода/вывода данных
     * @param roomManager менеджер комнат для выполнения выселения
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void checkOutGuests(ConsoleView view, RoomManager roomManager, Scanner scanner) {
        view.print("Введите номер комнаты для выселения: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        boolean success = roomManager.checkOut(roomNumber);
        if (success) view.println("Гости из комнаты " + roomNumber + " успешно выселены.");
        else view.println("Ошибка: комната не найдена или пуста.");
    }

    /**
     * Добавляет новый номер в систему отеля.
     * @param view представление для ввода/вывода данных
     * @param controller контроллер комнат для добавления нового номера
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void addNewRoom(ConsoleView view, RoomController controller, Scanner scanner) {
        view.print("Номер комнаты: ");
        int number = Integer.parseInt(scanner.nextLine());
        view.print("Вместимость: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        view.print("Звезды: ");
        int stars = Integer.parseInt(scanner.nextLine());
        view.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());
        Room room = new Room(0, number, capacity, price, stars);
        boolean added = controller.addRoom(room);
        if (added) view.println("Номер добавлен успешно");
        else view.println("Номер с таким номером уже существует");
    }

    /**
     * Добавляет услугу конкретному гостю.
     * Показывает список доступных услуг и позволяет выбрать одну для добавления.
     * Проверяет существование гостя и услуги перед выполнением операции.
     * @param view представление для ввода/вывода данных
     * @param guestManager менеджер гостей для поиска гостя
     * @param guestController контроллер гостей для добавления услуги
     * @param serviceManager менеджер услуг для получения списка услуг
     * @param scanner сканер для чтения пользовательского ввода
     */
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

    /**
     * Импортирует гостей из CSV файла.
     */
    private void importGuests() throws Exception {
        consoleView.print("Введите путь к CSV-файлу гостей: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.importGuests(path);
        consoleView.println("Импорт гостей успешно завершён!");
    }

    /**
     * Импортирует комнаты из CSV файла.
     */
    private void importRooms() throws Exception {
        consoleView.print("Введите путь к CSV-файлу комнат: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.importRooms(path);
        consoleView.println("Импорт комнат успешно завершён!");
    }

    /**
     * Импортирует услуги из CSV файла.
     */
    private void importServices() throws Exception {
        consoleView.print("Введите путь к CSV-файлу услуг: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.importServices(path);
        consoleView.println("Импорт услуг успешно завершён!");
    }

    /**
     * Экспортирует комнаты в CSV файл.
     */
    private void exportRooms() throws Exception {
        consoleView.print("Введите путь для сохранения CSV комнат: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.exportRooms(path);
        consoleView.println("Экспорт комнат успешно завершён!");
    }

    /**
     * Экспортирует гостей в CSV файл.
     */
    private void exportGuests() throws Exception {
        consoleView.print("Введите путь для сохранения CSV гостей: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.exportGuests(path);
        consoleView.println("Экспорт гостей успешно завершён!");
    }

    /**
     * Экспортирует услуги в CSV файл.
     */
    private void exportServices() throws Exception {
        consoleView.print("Введите путь для сохранения CSV услуг: ");
        String path = new Scanner(System.in).nextLine().trim();
        importExportController.exportServices(path);
        consoleView.println("Экспорт услуг успешно завершён!");
    }

}
