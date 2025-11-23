package task_7.ui;

import task_7.controller.GuestController;
import task_7.controller.ImportExportController;
import task_7.controller.RoomController;
import task_7.controller.ServiceController;
import task_7.exceptions.HotelException;
import task_7.exceptions.ValidationException;
import task_7.exceptions.guests.GuestAlreadyCheckedInException;
import task_7.exceptions.guests.GuestNotCheckedInException;
import task_7.exceptions.guests.GuestNotFoundException;
import task_7.exceptions.io.FileNotFoundException;
import task_7.exceptions.io.ImportExportException;
import task_7.exceptions.io.InvalidFileFormatException;
import task_7.exceptions.rooms.*;
import task_7.exceptions.services.ServiceAlreadyExistsException;
import task_7.exceptions.services.ServiceNotFoundException;
import task_7.model.Guest;
import task_7.model.Room;
import task_7.model.Service;
import task_7.service.GuestManager;
import task_7.service.RoomManager;
import task_7.service.ServiceManager;
import task_7.view.ConsoleView;
import task_7.view.enums.GuestSortOption;
import task_7.view.enums.RoomSortOption;
import task_7.view.enums.ServiceSortOption;

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
    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;
    private final Scanner in;

    public ConsoleUI(ConsoleView consoleView,
                     GuestController guestController,
                     RoomController roomController,
                     ServiceController serviceController,
                     GuestManager guestManager,
                     RoomManager roomManager,
                     ServiceManager serviceManager) {
        this.consoleView = consoleView;
        this.guestController = guestController;
        this.roomController = roomController;
        this.serviceController = serviceController;
        this.guestManager = guestManager;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
        this.in = new Scanner(System.in);
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

        boolean running = true;

        while (running) {
            consoleView.println("""
                    
                    ===== Главное меню =====
                    1 — Гости
                    2 — Комнаты
                    3 — Услуги
                    0 — Выход
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            switch (input) {
                case "1" -> guestMenu();
                case "2" -> roomMenu();
                case "3" -> serviceMenu();
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
                    case "1" -> registerGuestOnly(in);
                    case "2" -> showAllGuests(in);
                    case "3" -> showGuestsNotCheckedIn();
                    case "4" -> showGuestsCheckedIn();
                    case "5" -> checkInGuestToRoom(in);
                    case "6" -> checkOutGuestFromRoom(in);
                    case "7" -> showGuestServices(in);
                    case "8" -> addServiceToGuest(in);
                    case "9" -> showGuestsCount();
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные " + e.getMessage());
            } catch (GuestNotFoundException e) {
                consoleView.printError(e.getMessage());
            } catch (GuestAlreadyCheckedInException | GuestNotCheckedInException e) {
                consoleView.printError(e.getMessage());
            } catch (RoomNotFoundException | RoomOccupiedException | RoomUnderMaintenanceException |
                     RoomCapacityExceededException e) {
                consoleView.printError("Ошибка с комнатой " + e.getMessage());
            } catch (ServiceNotFoundException e) {
                consoleView.printError(e.getMessage());
            } catch (HotelException e) {
                consoleView.printError("Ошибка системы " + e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Необработанное исключение в guestMenu: " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Меню управления комнатами.
     * Предоставляет операции просмотра, добавления, управления состоянием комнат.
     */
    private void roomMenu() {

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
                    case "1" -> showAllRooms(in);
                    case "2" -> showFreeRooms(in);
                    case "3" -> addNewRoom(in);
                    case "4" -> checkOutGuests(in);
                    case "5" -> showRoomHistory(in);
                    case "6" -> showRoomDetails(in);
                    case "7" -> findRoomsFreeByDate(in);
                    case "8" -> calculateFullRoomPrice(in);
                    case "9" -> consoleView.println("Свободных номеров: " + roomController.countFreeRooms());
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные " + e.getMessage());
            } catch (RoomNotFoundException | RoomAlreadyExistsException | RoomOccupiedException e) {
                consoleView.printError("Ошибка с комнатой " + e.getMessage());
            } catch (HotelException e) {
                consoleView.printError("Ошибка системы " + e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Необработанное исключение в guestMenu: " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Меню управления услугами.
     * Предоставляет операции просмотра и добавления услуг.
     */
    private void serviceMenu() {

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
                    case "1" -> showAllServices(in);
                    case "2" -> addService(in);
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные " + e.getMessage());
            } catch (ServiceNotFoundException | ServiceAlreadyExistsException e) {
                consoleView.printError(e.getMessage());
            } catch (HotelException e) {
                consoleView.printError("Ошибка системы " + e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Необработанное исключение в guestMenu: " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Отображает все комнаты с возможностью сортировки.
     * @param scanner сканер для ввода пользователя
     */
    private void showAllRooms(Scanner scanner) {
        consoleView.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = roomController.getAllRooms(option);
        rooms.forEach(consoleView::println);
    }

    /**
     * Отображает свободные комнаты с возможностью сортировки.
     * @param scanner сканер для ввода пользователя
     */
    private void showFreeRooms(Scanner scanner) {
        consoleView.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
        String sortInput = scanner.nextLine();
        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
        List<Room> rooms = roomController.getFreeRooms(option);
        rooms.forEach(consoleView::println);
    }

    /**
     * Регистрирует нового гостя в системе.
     * @param scanner сканер для ввода данных
     */
    private void registerGuestOnly(Scanner scanner) {
        consoleView.println("\n=== Регистрация нового гостя ===");
        consoleView.print("Имя: ");
        String firstName = scanner.nextLine();
        consoleView.print("Фамилия: ");
        String lastName = scanner.nextLine();
        consoleView.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        Guest guest = new Guest(0, age, firstName, lastName, null, new ArrayList<>());
        guestController.registerGuest(guest);
        consoleView.println("Гость зарегистрирован в системе: " + guest.getFullName() + " (ID: " + guest.getId() + ")");
    }

    /**
     * Заселяет гостя в комнату.
     * Показывает список доступных гостей и обрабатывает процесс заселения.
     * @param scanner сканер для ввода данных
     */
    private void checkInGuestToRoom(Scanner scanner) {
        consoleView.println("\n=== Заселение гостя в комнату ===");

        List<Guest> availableGuests = guestManager.getGuestsNotCheckedIn();
        if (availableGuests.isEmpty()) {
            consoleView.println("Нет незаселенных гостей для заселения.");
            return;
        }

        consoleView.println("Доступные гости для заселения:");
        for (Guest guest : availableGuests) {
            consoleView.println("ID: " + guest.getId() + " - " + guest.getFullName() + ", возраст: " + guest.getAge());
        }

        consoleView.print("Выберите ID гостя для заселения: ");
        int guestId = Integer.parseInt(scanner.nextLine());

        consoleView.print("Номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());

        consoleView.print("Дата заселения (гггг-мм-дд, сегодня - пусто): ");
        String checkInInput = scanner.nextLine();
        LocalDate checkIn = checkInInput.isEmpty() ? LocalDate.now() : LocalDate.parse(checkInInput);

        consoleView.print("Дата выселения (гггг-мм-дд, +3 дня - пусто): ");
        String checkOutInput = scanner.nextLine();
        LocalDate checkOut = checkOutInput.isEmpty() ? checkIn.plusDays(3) : LocalDate.parse(checkOutInput);

        guestController.checkInGuest(guestId, roomNumber, checkIn, checkOut);
        consoleView.println("Гость успешно заселен в комнату " + roomNumber);
    }

    /**
     * Выселяет гостя из комнаты.
     * Показывает список заселенных гостей и обрабатывает процесс выселения.
     * @param scanner сканер для ввода данных
     */
    private void checkOutGuestFromRoom(Scanner scanner) {
        consoleView.println("\n=== Выселение гостя из комнаты ===");

        List<Guest> checkedInGuests = guestManager.getGuestsCheckedIn();
        if (checkedInGuests.isEmpty()) {
            consoleView.println("Нет заселенных гостей.");
            return;
        }

        consoleView.println("Заселенные гости:");
        for (Guest guest : checkedInGuests) {
            consoleView.println("ID: " + guest.getId() + " - " + guest.getFullName() +
                    ", комната: " + guest.getGuestRoom().getNumber());
        }

        consoleView.print("Выберите ID гостя для выселения: ");
        int guestId = Integer.parseInt(scanner.nextLine());
        guestController.checkOutGuest(guestId);
        consoleView.println("Гость успешно выселен из комнаты.");
    }

    /**
     * Отображает гостей, не заселенных в комнаты.
     */
    private void showGuestsNotCheckedIn() {
        List<Guest> guests = guestManager.getGuestsNotCheckedIn();
        if (guests.isEmpty()) {
            consoleView.println("Нет незаселенных гостей.");
        } else {
            consoleView.println("Незаселенные гости:");
            guests.forEach(g -> consoleView.println(" - " + g.getFullName() + ", возраст: " + g.getAge()));
        }
    }

    /**
     * Отображает количество гостей в системе.
     */
    private void showGuestsCount() {
        consoleView.println("Количество гостей: " + guestController.countGuests());
    }

    /**
     * Отображает заселенных гостей.
     */
    private void showGuestsCheckedIn() {
        List<Guest> guests = guestManager.getGuestsCheckedIn();
        if (guests.isEmpty()) {
            consoleView.println("Нет заселенных гостей.");
        } else {
            consoleView.println("Заселенные гости:");
            guests.forEach(g -> consoleView.println(" - " + g.getFullName() +
                    ", комната: " + g.getGuestRoom().getNumber()));
        }
    }

    /**
     * Отображает всех гостей с возможностью сортировки по различным критериям.
     * Пользователь выбирает критерий сортировки из доступных опций.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showAllGuests(Scanner scanner) {
        consoleView.print("Критерий сортировки (Алфавит, Дата освобождения номера): ");
        String sortInput = scanner.nextLine();
        GuestSortOption option = GuestSortOption.fromDescription(sortInput);
        List<Guest> guests = guestController.getSortedGuests(option);
        guests.forEach(consoleView::println);
    }

    /**
     * Добавляет новую услугу в систему.
     * Запрашивает у пользователя название, описание и цену услуги.
     * Создает услугу с текущей датой и сохраняет через контроллер.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void addService(Scanner scanner) {
        consoleView.print("Название услуги: ");
        String name = scanner.nextLine();
        consoleView.print("Описание: ");
        String description = scanner.nextLine();
        consoleView.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());
        Service service = new Service(0, name, description, price, LocalDate.now());
        serviceController.addService(service);
        consoleView.println("Услуга добавлена: " + service);
    }

    /**
     * Отображает все услуги с возможностью сортировки по цене или названию.
     * Пользователь выбирает критерий сортировки из доступных опций.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showAllServices(Scanner scanner) {
        consoleView.print("Критерий сортировки (Цена, Название): ");
        String sortInput = scanner.nextLine();
        ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
        List<Service> services = serviceController.getServices(option);
        services.forEach(consoleView::println);
    }

    /**
     * Находит и отображает номера, которые будут свободны к указанной дате.
     * Включает уже свободные номера и те, которые освободятся к заданной дате.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void findRoomsFreeByDate(Scanner scanner) {
        consoleView.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        List<Room> rooms = roomController.findRoomsThatWillBeFree(date);
        consoleView.println("Номера, которые будут свободны к " + date + ":");
        rooms.forEach(consoleView::println);
    }

    /**
     * Рассчитывает и отображает полную стоимость проживания в номере.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void calculateFullRoomPrice(Scanner scanner) {
        consoleView.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Double> price = roomController.getFullRoomPrice(roomNumber);
        price.ifPresentOrElse(
                p -> consoleView.println("Полная оплата за номер: " + p),
                () -> consoleView.println("Номер не найден")
        );
    }

    /**
     * Отображает историю проживания в указанной комнате.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showRoomHistory(Scanner scanner) {
        consoleView.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        consoleView.print("Введите количество последних гостей для истории: ");
        int historyLength = Integer.parseInt(scanner.nextLine());
        List<String> history = roomController.getRoomHistory(roomNumber, historyLength);
        history.forEach(consoleView::println);
    }

    /**
     * Отображает услуги конкретного гостя с возможностью сортировки.
     * Сначала находит гостя по имени, затем показывает его услуги.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showGuestServices(Scanner scanner) {
        consoleView.print("Введите имя и фамилию гостя: ");
        String guestName = scanner.nextLine();
        Guest guest = guestController.findGuestByFullName(guestName);

        consoleView.print("Критерий сортировки услуг (Цена, Название): ");
        String sortInput = scanner.nextLine();
        ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
        List<Service> services = guestController.getGuestServices(guest, option);
        services.forEach(consoleView::println);
    }

    /**
     * Отображает подробную информацию о конкретной комнате.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void showRoomDetails(Scanner scanner) {
        consoleView.print("Введите номер комнаты: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        Optional<Room> room = roomController.getFullRoomInfo(roomNumber);
        room.ifPresentOrElse(consoleView::println, () -> consoleView.println("Номер не найден"));
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void checkOutGuests(Scanner scanner) {
        consoleView.print("Введите номер комнаты для выселения: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        boolean success = roomManager.checkOut(roomNumber);
        if (success) consoleView.println("Гости из комнаты " + roomNumber + " успешно выселены.");
        else consoleView.println("Ошибка: комната не найдена или пуста.");
    }

    /**
     * Добавляет новый номер в систему отеля.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void addNewRoom(Scanner scanner) {
        consoleView.print("Номер комнаты: ");
        int number = Integer.parseInt(scanner.nextLine());
        consoleView.print("Вместимость: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        consoleView.print("Звезды: ");
        int stars = Integer.parseInt(scanner.nextLine());
        consoleView.print("Цена: ");
        double price = Double.parseDouble(scanner.nextLine());
        Room room = new Room(0, number, capacity, price, stars);
        boolean added = roomController.addRoom(room);
        if (added) consoleView.println("Номер добавлен успешно");
        else consoleView.println("Номер с таким номером уже существует");
    }

    /**
     * Добавляет услугу конкретному гостю.
     * Показывает список доступных услуг и позволяет выбрать одну для добавления.
     * Проверяет существование гостя и услуги перед выполнением операции.
     * @param scanner сканер для чтения пользовательского ввода
     */
    private void addServiceToGuest(Scanner scanner) {
        consoleView.print("Введите имя и фамилию гостя: ");
        String guestName = scanner.nextLine();

        List<Service> services = serviceManager.getSortedServices(ServiceSortOption.NAME);
        consoleView.println("Доступные услуги:");
        services.forEach(s -> consoleView.println("- " + s.getName() + " (Цена: " + s.getPrice() + ")"));

        consoleView.print("Введите название услуги: ");
        String serviceName = scanner.nextLine();

        guestController.addServiceToGuestByName(guestName, serviceName);
        consoleView.println("Услуга добавлена гостю.");
    }


}
