package task_11.ui;

import di.Component;
import di.Inject;
import task_11.controller.interfaces.IGuestController;
import task_11.controller.interfaces.IRoomController;
import task_11.controller.interfaces.IServiceController;
import task_11.exceptions.ValidationException;
import task_11.exceptions.guests.GuestNotCheckedInException;
import task_11.exceptions.guests.GuestNotFoundException;
import task_11.exceptions.rooms.*;
import task_11.exceptions.services.ServiceAlreadyExistsException;
import task_11.exceptions.services.ServiceNotFoundException;
import task_11.model.Guest;
import task_11.model.Room;
import task_11.model.Service;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;
import task_11.view.ConsoleView;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.RoomSortOption;
import task_11.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Основной пользовательский интерфейс консольного приложения отеля.
 * Координирует взаимодействие между пользователем и системой через консоль.
 */
@Component
public class ConsoleUI {

    @Inject
    private ConsoleView consoleView;

    @Inject
    private IGuestController guestController;

    @Inject
    private IRoomController roomController;

    @Inject
    private IServiceController serviceController;

    @Inject
    private IGuestManager guestManager;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    private Scanner in = new Scanner(System.in);

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
                    case "1" -> registerGuestOnly();
                    case "2" -> showAllGuests();
                    case "3" -> showGuestsNotCheckedIn();
                    case "4" -> showGuestsCheckedIn();
                    case "5" -> checkInGuestToRoom();
                    case "6" -> checkOutGuestFromRoom();
                    case "7" -> showGuestServices();
                    case "8" -> addServiceToGuest();
                    case "9" -> showGuestsCount();
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные: " + e.getMessage());
            } catch (GuestNotFoundException e) {
                consoleView.printError(e.getMessage());
            } catch (GuestNotCheckedInException e) {
                consoleView.printError(e.getMessage());
            } catch (RoomNotFoundException e) {
                consoleView.printError("Комната не найдена: " + e.getMessage());
            } catch (ServiceNotFoundException e) {
                consoleView.printError(e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Ошибка: " + e.getMessage());
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
                    10 - Изменить статус обслуживания
                    0 — Назад
                    """);

            consoleView.print("Выберите действие: ");
            String input = in.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> showAllRooms();
                    case "2" -> showFreeRooms();
                    case "3" -> addNewRoom();
                    case "4" -> checkOutGuests();
                    case "5" -> showRoomHistory();
                    case "6" -> showRoomDetails();
                    case "7" -> findRoomsFreeByDate();
                    case "8" -> calculateFullRoomPrice();
                    case "9" -> consoleView.println("Свободных номеров: " + roomController.countFreeRooms());
                    case "10" -> changeRoomMaintenance();
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные: " + e.getMessage());
            } catch (RoomNotFoundException e) {
                consoleView.printError("Комната не найдена: " + e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Ошибка: " + e.getMessage());
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
                    case "1" -> showAllServices();
                    case "2" -> addService();
                    case "0" -> back = true;
                    default -> consoleView.printInvalidOption();
                }
            } catch (ValidationException e) {
                consoleView.printError("Некорректные данные: " + e.getMessage());
            } catch (ServiceAlreadyExistsException e) {
                consoleView.printError("Услуга уже существует: " + e.getMessage());
            } catch (Exception e) {
                consoleView.printError("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Отображает все комнаты с возможностью сортировки.
     */
    private void showAllRooms() {
        consoleView.print("Критерий сортировки (Номер, Цена, Звезды): ");
        String sortInput = in.nextLine().trim();
        RoomSortOption option = parseRoomSortOption(sortInput);
        List<Room> rooms = roomController.getAllRooms(option);
        if (rooms.isEmpty()) {
            consoleView.println("Нет зарегистрированных комнат.");
        } else {
            consoleView.println("Все комнаты отеля:");
            rooms.forEach(room -> consoleView.println(
                    "Номер: " + room.getNumber() +
                            ", Вместимость: " + room.getCapacity() +
                            ", Цена: " + room.getPrice() +
                            ", Звезды: " + room.getStars() +
                            ", Статус: " + (room.isOccupied() ? "Занята" : "Свободна") +
                            (room.isUnderMaintenance() ? " (На обслуживании)" : "")
            ));
        }
    }

    /**
     * Отображает свободные комнаты с возможностью сортировки.
     */
    private void showFreeRooms() {
        consoleView.print("Критерий сортировки (Номер, Цена, Звезды): ");
        String sortInput = in.nextLine().trim();
        RoomSortOption option = parseRoomSortOption(sortInput);
        List<Room> rooms = roomController.getFreeRooms(option);
        if (rooms.isEmpty()) {
            consoleView.println("Нет свободных номеров.");
        } else {
            consoleView.println("Свободные номера:");
            rooms.forEach(room -> consoleView.println(
                    "Номер: " + room.getNumber() +
                            ", Вместимость: " + room.getCapacity() +
                            ", Цена: " + room.getPrice() +
                            ", Звезды: " + room.getStars()
            ));
        }
    }

    /**
     * Регистрирует нового гостя в системе.
     */
    private void registerGuestOnly() {
        consoleView.println("\n=== Регистрация нового гостя ===");
        consoleView.print("Имя: ");
        String firstName = in.nextLine().trim();
        consoleView.print("Фамилия: ");
        String lastName = in.nextLine().trim();
        consoleView.print("Возраст: ");
        String ageInput = in.nextLine().trim();

        try {
            int age = Integer.parseInt(ageInput);
            Guest guest = new Guest(0, age, firstName, lastName, null, null, List.of());
            guestController.registerGuest(guest);
            consoleView.println("Гость зарегистрирован в системе: " + guest.getFullName() + " (ID: " + guest.getId() + ")");
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный возраст. Введите целое число.");
        }
    }

    /**
     * Заселяет гостя в комнату.
     * Показывает список доступных гостей и обрабатывает процесс заселения.
     */
    private void checkInGuestToRoom() {
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
        String guestIdInput = in.nextLine().trim();

        consoleView.print("Номер комнаты: ");
        String roomNumberInput = in.nextLine().trim();

        consoleView.print("Дата заселения (гггг-мм-дд, сегодня - пусто): ");
        String checkInInput = in.nextLine().trim();

        consoleView.print("Дата выселения (гггг-мм-дд, +3 дня - пусто): ");
        String checkOutInput = in.nextLine().trim();

        try {
            long guestId = Long.parseLong(guestIdInput);
            int roomNumber = Integer.parseInt(roomNumberInput);

            LocalDate checkIn = checkInInput.isEmpty() ? LocalDate.now() : LocalDate.parse(checkInInput);
            LocalDate checkOut = checkOutInput.isEmpty() ? checkIn.plusDays(3) : LocalDate.parse(checkOutInput);

            boolean success = guestController.checkInGuest(guestId, roomNumber, checkIn, checkOut);
            if (success) {
                consoleView.println("Гость успешно заселен в комнату " + roomNumber);
            } else {
                consoleView.println("Ошибка заселения. Проверьте номер комнаты и статус гостя.");
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный ввод ID гостя или номера комнаты.");
        }
    }

    /**
     * Выселяет гостя из комнаты.
     * Показывает список заселенных гостей и обрабатывает процесс выселения.
     */
    private void checkOutGuestFromRoom() {
        consoleView.println("\n=== Выселение гостя из комнаты ===");

        List<Guest> checkedInGuests = guestManager.getGuestsCheckedIn();
        if (checkedInGuests.isEmpty()) {
            consoleView.println("Нет заселенных гостей.");
            return;
        }

        consoleView.println("Заселенные гости:");
        for (Guest guest : checkedInGuests) {
            consoleView.println("ID: " + guest.getId() + " - " + guest.getFullName() +
                    ", комната: " + guest.getRoom().getNumber());
        }

        consoleView.print("Выберите ID гостя для выселения: ");
        String guestIdInput = in.nextLine().trim();

        try {
            long guestId = Long.parseLong(guestIdInput);
            guestController.checkOutGuest(guestId);
            consoleView.println("Гость успешно выселен из комнаты.");
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный ввод ID гостя.");
        } catch (GuestNotFoundException e) {
            consoleView.printError("Гость с указанным ID не найден.");
        } catch (GuestNotCheckedInException e) {
            consoleView.printError("Гость не заселен в комнату.");
        }
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
                    ", комната: " + g.getRoom().getNumber()));
        }
    }

    /**
     * Отображает всех гостей с возможностью сортировки по различным критериям.
     * Пользователь выбирает критерий сортировки из доступных опций.
     */
    private void showAllGuests() {
        consoleView.print("Критерий сортировки (Имя, Возраст, Номер комнаты): ");
        String sortInput = in.nextLine().trim();
        GuestSortOption option = parseGuestSortOption(sortInput);
        List<Guest> guests = guestController.getSortedGuests(option);

        if (guests.isEmpty()) {
            consoleView.println("Нет зарегистрированных гостей.");
        } else {
            consoleView.println("Все гости:");
            guests.forEach(guest -> {
                String roomInfo = (guest.getRoom() != null)
                        ? " (Комната: " + guest.getRoom().getNumber() + ")"
                        : " (Не заселен)";
                consoleView.println("ID: " + guest.getId() +
                        " - " + guest.getFullName() +
                        ", возраст: " + guest.getAge() +
                        roomInfo);
            });
        }
    }

    /**
     * Добавляет новую услугу в систему.
     * Запрашивает у пользователя название, описание и цену услуги.
     * Создает услугу с текущей датой и сохраняет через контроллер.
     */
    private void addService() {
        consoleView.println("\n=== Добавление новой услуги ===");
        consoleView.print("Название услуги: ");
        String name = in.nextLine().trim();
        consoleView.print("Описание: ");
        String description = in.nextLine().trim();
        consoleView.print("Цена: ");
        String priceInput = in.nextLine().trim();

        try {
            double price = Double.parseDouble(priceInput);
            Service service = new Service();
            service.setName(name);
            service.setDescription(description);
            service.setPrice(price);
            service.setDate(LocalDate.now());

            Service addedService = serviceController.addService(service);
            consoleView.println("Услуга добавлена: " + addedService.getName() + " (ID: " + addedService.getId() + ")");
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректная цена. Введите число.");
        }
    }

    /**
     * Отображает все услуги с возможностью сортировки по цене или названию.
     * Пользователь выбирает критерий сортировки из доступных опций.
     */
    private void showAllServices() {
        consoleView.print("Критерий сортировки (Название, Цена): ");
        String sortInput = in.nextLine().trim();
        ServiceSortOption option = parseServiceSortOption(sortInput);
        List<Service> services = serviceController.getServices(option);

        if (services.isEmpty()) {
            consoleView.println("Нет зарегистрированных услуг.");
        } else {
            consoleView.println("Все услуги:");
            services.forEach(service -> consoleView.println(
                    "ID: " + service.getId() +
                            ", Название: " + service.getName() +
                            ", Описание: " + service.getDescription() +
                            ", Цена: " + service.getPrice() +
                            ", Дата: " + service.getDate()
            ));
        }
    }

    /**
     * Находит и отображает номера, которые будут свободны к указанной дате.
     * Включает уже свободные номера и те, которые освободятся к заданной дате.
     */
    private void findRoomsFreeByDate() {
        consoleView.print("Введите дату (yyyy-mm-dd): ");
        String dateInput = in.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(dateInput);
            List<Room> rooms = roomController.findRoomsThatWillBeFree(date);

            if (rooms.isEmpty()) {
                consoleView.println("Нет номеров, которые будут свободны к " + date);
            } else {
                consoleView.println("Номера, которые будут свободны к " + date + ":");
                rooms.forEach(room -> consoleView.println(
                        "Номер: " + room.getNumber() +
                                ", Вместимость: " + room.getCapacity() +
                                ", Цена: " + room.getPrice() +
                                ", Статус: " + (room.isOccupied() ?
                                "Освободится " + room.getCheckOutDate() : "Свободна")
                ));
            }
        } catch (Exception e) {
            consoleView.printError("Некорректный формат даты. Используйте формат гггг-мм-дд.");
        }
    }

    /**
     * Рассчитывает и отображает полную стоимость проживания в номере.
     */
    private void calculateFullRoomPrice() {
        consoleView.print("Введите номер комнаты: ");
        String roomNumberInput = in.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumberInput);
            Optional<Double> price = roomController.getFullRoomPrice(roomNumber);

            if (price.isPresent()) {
                consoleView.println("Полная стоимость проживания в номере " + roomNumber + ": " + price.get());
            } else {
                consoleView.println("Номер " + roomNumber + " не найден.");
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный номер комнаты. Введите целое число.");
        }
    }

    /**
     * Изменяет статус обслуживания комнаты.
     */
    private void changeRoomMaintenance() {
        consoleView.print("Введите номер комнаты: ");
        String roomNumberInput = in.nextLine().trim();

        consoleView.print("Установить статус обслуживания (true/false): ");
        String maintenanceInput = in.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumberInput);
            boolean maintenance = Boolean.parseBoolean(maintenanceInput);

            roomController.setRoomMaintenance(roomNumber, maintenance);
            consoleView.println("Статус обслуживания номера " + roomNumber +
                    " изменен на: " + (maintenance ? "требует обслуживания" : "готов к заселению"));
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный номер комнаты. Введите целое число.");
        }
    }

    /**
     * Отображает историю проживания в указанной комнате.
     */
    private void showRoomHistory() {
        consoleView.print("Введите номер комнаты: ");
        String roomNumberInput = in.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumberInput);
            List<String> history = roomController.getRoomHistory(roomNumber);

            if (history.isEmpty()) {
                consoleView.println("История посещений для номера " + roomNumber + " отсутствует.");
            } else {
                consoleView.println("История посещений номера " + roomNumber + ":");
                for (int i = 0; i < history.size(); i++) {
                    consoleView.println((i + 1) + ". " + history.get(i));
                }
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный номер комнаты. Введите целое число.");
        }
    }

    /**
     * Отображает услуги конкретного гостя с возможностью сортировки.
     * Сначала находит гостя по имени, затем показывает его услуги.
     */
    private void showGuestServices() {
        consoleView.print("Введите имя и фамилию гостя: ");
        String guestName = in.nextLine().trim();

        try {
            Guest guest = guestController.findGuestByFullName(guestName);
            if (guest == null) {
                consoleView.println("Гость с именем '" + guestName + "' не найден.");
                return;
            }

            consoleView.print("Критерий сортировки услуг (Название, Цена): ");
            String sortInput = in.nextLine().trim();
            ServiceSortOption option = parseServiceSortOption(sortInput);

            List<Service> services = guestController.getGuestServices(guest, option);

            if (services.isEmpty()) {
                consoleView.println("У гостя " + guest.getFullName() + " нет услуг.");
            } else {
                consoleView.println("Услуги гостя " + guest.getFullName() + ":");
                services.forEach(service -> consoleView.println(
                        "- " + service.getName() +
                                " (Цена: " + service.getPrice() +
                                ", Дата: " + service.getDate() + ")"
                ));
            }
        } catch (Exception e) {
            consoleView.printError("Ошибка при получении услуг гостя: " + e.getMessage());
        }
    }

    /**
     * Отображает подробную информацию о конкретной комнате.
     */
    private void showRoomDetails() {
        consoleView.print("Введите номер комнаты: ");
        String roomNumberInput = in.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumberInput);
            Optional<Room> room = roomController.getFullRoomInfo(roomNumber);

            if (room.isPresent()) {
                Room r = room.get();
                consoleView.println("Детальная информация о номере " + r.getNumber() + ":");
                consoleView.println("Вместимость: " + r.getCapacity());
                consoleView.println("Цена за ночь: " + r.getPrice());
                consoleView.println("Звезды: " + r.getStars());
                consoleView.println("Статус: " + (r.isOccupied() ? "Занят" : "Свободен"));
                consoleView.println("Техническое обслуживание: " + (r.isUnderMaintenance() ? "Требуется" : "Не требуется"));

                if (r.isOccupied() && r.getCheckInDate() != null && r.getCheckOutDate() != null) {
                    consoleView.println("Дата заселения: " + r.getCheckInDate());
                    consoleView.println("Дата выселения: " + r.getCheckOutDate());
                    consoleView.println("Гостей в номере: " + r.getGuests().size());
                }
            } else {
                consoleView.println("Номер " + roomNumber + " не найден.");
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный номер комнаты. Введите целое число.");
        }
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     */
    private void checkOutGuests() {
        consoleView.print("Введите номер комнаты для выселения: ");
        String roomNumberInput = in.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumberInput);
            boolean success = roomManager.checkOut(roomNumber);

            if (success) {
                consoleView.println("Все гости из комнаты " + roomNumber + " успешно выселены.");
            } else {
                consoleView.println("Ошибка: комната не найдена, пуста или уже свободна.");
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный номер комнаты. Введите целое число.");
        }
    }

    /**
     * Добавляет новый номер в систему отеля.
     */
    private void addNewRoom() {
        consoleView.println("\n=== Добавление нового номера ===");
        consoleView.print("Номер комнаты: ");
        String numberInput = in.nextLine().trim();

        consoleView.print("Вместимость: ");
        String capacityInput = in.nextLine().trim();

        consoleView.print("Звезды (1-5): ");
        String starsInput = in.nextLine().trim();

        consoleView.print("Цена за ночь: ");
        String priceInput = in.nextLine().trim();

        try {
            int number = Integer.parseInt(numberInput);
            int capacity = Integer.parseInt(capacityInput);
            int stars = Integer.parseInt(starsInput);
            double price = Double.parseDouble(priceInput);

            Room room = new Room();
            room.setNumber(number);
            room.setCapacity(capacity);
            room.setStars(stars);
            room.setPrice(price);

            boolean added = roomController.addRoom(room);

            if (added) {
                consoleView.println("Номер " + number + " успешно добавлен в систему.");
            } else {
                consoleView.println("Ошибка: номер с таким номером уже существует.");
            }
        } catch (NumberFormatException e) {
            consoleView.printError("Некорректный ввод. Все числовые поля должны содержать числа.");
        } catch (ValidationException e) {
            consoleView.printError("Ошибка валидации: " + e.getMessage());
        }
    }

    /**
     * Добавляет услугу конкретному гостю.
     * Показывает список доступных услуг и позволяет выбрать одну для добавления.
     * Проверяет существование гостя и услуги перед выполнением операции.
     */
    private void addServiceToGuest() {
        consoleView.print("Введите имя и фамилию гостя: ");
        String guestName = in.nextLine().trim();

        consoleView.println("Доступные услуги:");
        List<Service> services = serviceController.getServices(ServiceSortOption.BY_NAME);
        if (services.isEmpty()) {
            consoleView.println("Нет доступных услуг.");
            return;
        }

        services.forEach(s -> consoleView.println("- " + s.getName() + " (Цена: " + s.getPrice() + ")"));

        consoleView.print("Введите название услуги: ");
        String serviceName = in.nextLine().trim();

        try {
            guestController.addServiceToGuestByName(guestName, serviceName);
            consoleView.println("Услуга '" + serviceName + "' успешно добавлена гостю '" + guestName + "'.");
        } catch (ServiceNotFoundException e) {
            consoleView.printError("Услуга '" + serviceName + "' не найдена.");
        } catch (GuestNotFoundException e) {
            consoleView.printError("Гость '" + guestName + "' не найден.");
        }
    }

    /**
     * Преобразует строку в опцию сортировки для комнат.
     */
    private RoomSortOption parseRoomSortOption(String input) {
        return switch (input.toLowerCase()) {
            case "номер", "number" -> RoomSortOption.BY_NUMBER;
            case "цена", "price" -> RoomSortOption.BY_PRICE;
            case "звезды", "stars" -> RoomSortOption.BY_STARS;
            default -> RoomSortOption.BY_NUMBER;
        };
    }

    /**
     * Преобразует строку в опцию сортировки для гостей.
     */
    private GuestSortOption parseGuestSortOption(String input) {
        return switch (input.toLowerCase()) {
            case "имя", "name" -> GuestSortOption.BY_NAME;
            case "возраст", "age" -> GuestSortOption.BY_AGE;
            case "номер комнаты", "room number" -> GuestSortOption.BY_ROOM_NUMBER;
            default -> GuestSortOption.BY_NAME;
        };
    }

    /**
     * Преобразует строку в опцию сортировки для услуг.
     */
    private ServiceSortOption parseServiceSortOption(String input) {
        return switch (input.toLowerCase()) {
            case "название", "name" -> ServiceSortOption.BY_NAME;
            case "цена", "price" -> ServiceSortOption.BY_PRICE;
            default -> ServiceSortOption.BY_NAME;
        };
    }
}
