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

        Scanner scanner = new Scanner(System.in);
        consoleView.printWelcome();

        boolean running = true;
        while (running) {
            consoleView.println("\n===== Главное меню =====");
            consoleView.println("1 — Показать все номера");
            consoleView.println("2 — Показать свободные номера");
            consoleView.println("3 — Зарегистрировать гостя");
            consoleView.println("4 — Показать всех гостей");
            consoleView.println("5 — Добавить услугу");
            consoleView.println("6 — Показать все услуги");
            consoleView.println("7 — Узнать количество свободных номеров");
            consoleView.println("8 — Узнать количество гостей");
            consoleView.println("9 — Найти номера, которые будут свободны к дате");
            consoleView.println("10 — Рассчитать полную оплату за номер");
            consoleView.println("11 — Показать историю последних гостей номера");
            consoleView.println("12 — Показать услуги гостя");
            consoleView.println("13 — Показать подробности номера");
            consoleView.println("14 — Выселить гостей из комнаты");
            consoleView.println("15 - Добавить новую комнату");
            consoleView.println("16 - Добавить гостю существующую услугу");
            consoleView.println("0 — Выход");
            consoleView.print("Выберите действие: ");

            String input = scanner.nextLine().trim();
            try {
                switch (input) {
                    case "1" -> { /* Показать все номера */
                        consoleView.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
                        String sortInput = scanner.nextLine();
                        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
                        List<Room> rooms = roomController.getAllRooms(option);
                        rooms.forEach(consoleView::println);
                    }

                    case "2" -> { /* Показать свободные номера */
                        consoleView.print("Критерий сортировки (Цена, Вместимость, Звезды): ");
                        String sortInput = scanner.nextLine();
                        RoomSortOption option = RoomSortOption.fromDescription(sortInput);
                        List<Room> rooms = roomController.getFreeRooms(option);
                        rooms.forEach(consoleView::println);
                    }

                    case "3" -> { /* Регистрация гостя */
                        consoleView.print("Имя: "); String firstName = scanner.nextLine();
                        consoleView.print("Фамилия: "); String lastName = scanner.nextLine();
                        consoleView.print("Возраст: "); int age = Integer.parseInt(scanner.nextLine());
                        consoleView.print("Номер комнаты: "); int roomNumber = Integer.parseInt(scanner.nextLine());

                        Guest guest = guestController.registerGuest(firstName, lastName, age, roomNumber,
                                LocalDate.now(), LocalDate.now().plusDays(3));
                        if (guest != null) {
                            consoleView.println("Гость зарегистрирован: " + guest);
                        } else {
                            consoleView.println("Ошибка: комната не найдена или занята. Регистрация не прошла.");
                        }
                    }

                    case "4" -> { /* Список всех гостей */
                        consoleView.print("Критерий сортировки (Алфавит, Дата освобождения номера): ");
                        String sortInput = scanner.nextLine();
                        GuestSortOption option = GuestSortOption.fromDescription(sortInput);
                        List<Guest> guests = guestController.getSortedGuests(option);
                        guests.forEach(consoleView::println);
                    }

                    case "5" -> { /* Добавление услуги */
                        consoleView.print("Название услуги: "); String name = scanner.nextLine();
                        consoleView.print("Описание: "); String desc = scanner.nextLine();
                        consoleView.print("Цена: "); double price = Double.parseDouble(scanner.nextLine());
                        Service service = serviceController.addService(name, desc, price, LocalDate.now());
                        consoleView.println("Услуга добавлена: " + service);
                    }

                    case "6" -> { /* Показать все услуги */
                        consoleView.print("Критерий сортировки (Цена, Название): ");
                        String sortInput = scanner.nextLine();
                        ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
                        List<Service> services = serviceController.getServices(option);
                        services.forEach(consoleView::println);
                    }

                    case "7" -> consoleView.println("Количество свободных номеров: " + roomController.countFreeRooms());

                    case "8" -> consoleView.println("Количество гостей: " + guestController.countGuests());

                    case "9" -> { /* Свободные номера к определенной дате */
                        consoleView.print("Введите дату (yyyy-mm-dd): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine());
                        List<Room> rooms = roomController.findRoomsThatWillBeFree(date);
                        consoleView.println("Номера, которые будут свободны к " + date + ":");
                        rooms.forEach(consoleView::println);
                    }

                    case "10" -> { /* Расчет стоимости */
                        consoleView.print("Введите номер комнаты: ");
                        int roomNumber = Integer.parseInt(scanner.nextLine());
                        Optional<Double> price = roomController.getFullRoomPrice(roomNumber);
                        price.ifPresentOrElse(
                                p -> consoleView.println("Полная оплата за номер: " + p),
                                () -> consoleView.println("Номер не найден")
                        );
                    }

                    case "11" -> { /* История гостей */
                        consoleView.print("Введите номер комнаты: ");
                        int roomNumber = Integer.parseInt(scanner.nextLine());
                        consoleView.print("Введите количество последних гостей для истории: ");
                        int historyLength = Integer.parseInt(scanner.nextLine());
                        List<String> history = roomController.getRoomHistory(roomNumber, historyLength);
                        history.forEach(consoleView::println);
                    }

                    case "12" -> { /* Просмотр услуг гостя */
                        consoleView.print("Введите имя и фамилию гостя: ");
                        String guestName = scanner.nextLine();
                        Guest guest = guestController.findGuestByFullName(guestName);
                        if (guest != null) {
                            consoleView.print("Критерий сортировки услуг (Цена, Название): ");
                            String sortInput = scanner.nextLine();
                            ServiceSortOption option = ServiceSortOption.fromDescription(sortInput);
                            List<Service> services = guestController.getGuestServices(guest, option);
                            services.forEach(consoleView::println);
                        } else {
                            consoleView.println("Гость не найден");
                        }
                    }

                    case "13" -> { /* Подробности по номеру */
                        consoleView.print("Введите номер комнаты: ");
                        int roomNumber = Integer.parseInt(scanner.nextLine());
                        Optional<Room> room = roomController.getFullRoomInfo(roomNumber);
                        room.ifPresentOrElse(consoleView::println, () -> consoleView.println("Номер не найден"));
                    }

                    case "14" -> { /* Выселение гостей */
                        consoleView.print("Введите номер комнаты для выселения: ");
                        int roomNumber = Integer.parseInt(scanner.nextLine());
                        boolean success = roomManager.checkOut(roomNumber);
                        if (success) {
                            consoleView.println("Гости из комнаты " + roomNumber + " успешно выселены.");
                        } else {
                            consoleView.println("Ошибка: комната не найдена или пуста.");
                        }
                    }

                    case "15" -> { /* Добавление комнаты */
                        consoleView.print("Номер комнаты: ");
                        int number = Integer.parseInt(scanner.nextLine());
                        consoleView.print("Вместимость: ");
                        int capacity = Integer.parseInt(scanner.nextLine());
                        consoleView.print("Звезды: ");
                        int stars = Integer.parseInt(scanner.nextLine());
                        consoleView.print("Цена: ");
                        double price = Double.parseDouble(scanner.nextLine());

                        boolean added = roomController.addRoom(number, capacity, stars, price);
                        if (added) consoleView.println("Номер добавлен успешно");
                        else consoleView.println("Номер с таким номером уже существует");
                    }

                    case "16" -> { /* Добавление услуги гостю */
                        consoleView.print("Введите имя и фамилию гостя: ");
                        String guestName = scanner.nextLine();
                        Guest guest = guestManager.findGuestByFullName(guestName);

                        if (guest == null) {
                            consoleView.println("Гость не найден.");
                            break;
                        }

                        List<Service> services = serviceManager.getSortedServices(ServiceSortOption.NAME);
                        consoleView.println("Доступные услуги:");
                        services.forEach(s -> consoleView.println("- " + s.getName() + " (Цена: " + s.getPrice() + ")"));

                        consoleView.print("Введите название услуги: ");
                        String serviceName = scanner.nextLine();

                        boolean ok = guestController.addServiceToGuestByName(guestName, serviceName);
                        if (ok) {
                            consoleView.println("Услуга добавлена гостю.");
                        } else {
                            consoleView.println("Услуга не найдена.");
                        }
                    }

                    case "0" -> {
                        running = false;
                        consoleView.printGoodbye();
                    }
                    default -> consoleView.printInvalidOption();
                }
            } catch (Exception e) {
                // Центральная обработка ошибок, чтобы приложение не падало
                consoleView.println("Ошибка: " + e.getMessage());
        }
    }
    }
}