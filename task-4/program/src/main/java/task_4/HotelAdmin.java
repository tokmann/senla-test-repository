package task_4;

import task_4.management.HotelManager;
import task_4.model.Guest;
import task_4.model.Room;
import task_4.model.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HotelAdmin {
    public static void main(String[] args) {
        HotelManager hotelManager = new HotelManager();

        System.out.println("=== ТЕСТИРОВАНИЕ СИСТЕМЫ ГОСТИНИЦЫ ===\n");

        // Подготовка тестовых данных
        setupTestData(hotelManager);

        // Тестирование всех команд
        testRoomManagement(hotelManager);
        testGuestManagement(hotelManager);
        testServiceManagement(hotelManager);
        testQueryFunctions(hotelManager);
        testBookingOperations(hotelManager);

        System.out.println("=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }

    private static void setupTestData(HotelManager hotelManager) {
        System.out.println("1. ПОДГОТОВКА ТЕСТОВЫХ ДАННЫХ");

        hotelManager.getRoomManager().addRoom(101, 2, 2500.0, 3);
        hotelManager.getRoomManager().addRoom(102, 3, 3500.0, 4);
        hotelManager.getRoomManager().addRoom(103, 1, 1500.0, 2);
        hotelManager.getRoomManager().addRoom(201, 2, 5000.0, 5);
        hotelManager.getRoomManager().addRoom(202, 4, 4500.0, 4);

        hotelManager.getServiceManager().addService("Завтрак", "Континентальный завтрак", 500.0, LocalDate.now());
        hotelManager.getServiceManager().addService("SPA", "Спа процедуры", 2000.0, LocalDate.now().plusDays(2));

        hotelManager.getServiceManager().addService("Трансфер", "Трансфер из аэропорта", 1500.0, LocalDate.now().plusDays(5));

        hotelManager.getServiceManager().addService("Прачечная", "Стирка и глажка", 300.0, LocalDate.now().plusDays(8));

        System.out.println("Тестовые данные добавлены\n");
    }

    private static void testRoomManagement(HotelManager hotelManager) {
        System.out.println("2. ТЕСТИРОВАНИЕ УПРАВЛЕНИЯ НОМЕРАМИ");

        // 1. Список всех номеров с разной сортировкой
        System.out.println("1.1 Все номера (сортировка по цене):");
        hotelManager.showAllRooms("Цена");

        System.out.println("1.2 Все номера (сортировка по вместимости):");
        hotelManager.showAllRooms("Вместимость");

        System.out.println("1.3 Все номера (сортировка по звездам):");
        hotelManager.showAllRooms("Звезды");

        // 2. Список свободных номеров
        System.out.println("2.1 Свободные номера (сортировка по цене):");
        hotelManager.showAllFreeRooms("Цена");

        // 4. Общее число свободных номеров
        System.out.println("4. Общее число свободных номеров: " + hotelManager.countFreeRooms());

        // 11. Детали отдельного номера
        System.out.println("11. Детали номера 101:");
        hotelManager.showFullRoomInfo(101);

        System.out.println("--- Управление номерами завершено ---\n");
    }

    private static void testGuestManagement(HotelManager hotelManager) {
        System.out.println("3. ТЕСТИРОВАНИЕ УПРАВЛЕНИЯ ГОСТЯМИ");

        Service breakfast = new Service("Завтрак", "Континентальный завтрак", 500.0, LocalDate.now().plusDays(5));
        Service spa = new Service("SPA", "Спа процедуры", 2000.0, LocalDate.now().plusDays(8));

        List<Service> ivanServices = new ArrayList<>();
        ivanServices.add(breakfast);

        List<Service> annaServices = new ArrayList<>();
        annaServices.add(breakfast);
        annaServices.add(spa);

        Guest guest1 = new Guest(30, "Иван", "Петров", null, ivanServices);
        Guest guest2 = new Guest(25, "Анна", "Сидорова", null, annaServices);
        Guest guest3 = new Guest(40, "Петр", "Иванов", null, new ArrayList<>());

        hotelManager.addGuest(guest1);
        hotelManager.addGuest(guest2);
        hotelManager.addGuest(guest3);

        // 5. Общее число постояльцев
        System.out.println("5. Общее число постояльцев: " + hotelManager.countGuests());

        // 3. Список постояльцев по алфавиту
        System.out.println("3.1 Список гостей (по алфавиту):");
        hotelManager.showAllSortedGuests("Алфавит");

        System.out.println("--- Управление гостями завершено ---\n");
    }

    private static void testServiceManagement(HotelManager hotelManager) {
        System.out.println("4. ТЕСТИРОВАНИЕ УПРАВЛЕНИЯ УСЛУГАМИ");

        // 10. Цены всех услуг с сортировкой
        System.out.println("10.1 Все услуги (сортировка по цене):");
        List<Service> servicesByPrice = hotelManager.getSortedServices("Цена");
        servicesByPrice.forEach(System.out::println);

        System.out.println("10.2 Все услуги (сортировка по разделу):");
        List<Service> servicesByName = hotelManager.getSortedServices("Раздел");
        servicesByName.forEach(System.out::println);

        System.out.println("Изменение цены услуги 'Завтрак':");
        hotelManager.getServiceManager().changeServicePrice("Завтрак", 600.0);

        System.out.println("--- Управление услугами завершено ---\n");
    }

    private static void testQueryFunctions(HotelManager hotelManager) {
        System.out.println("5. ТЕСТИРОВАНИЕ ФУНКЦИЙ ЗАПРОСОВ");

        // 6. Номера, которые будут свободны по определенной дате
        LocalDate futureDate = LocalDate.now().plusDays(5);
        System.out.println("6. Номера, свободные к дате " + futureDate + ":");
        List<Room> freeRooms = hotelManager.findRoomsThatWillBeFree(futureDate);
        if (freeRooms.isEmpty()) {
            System.out.println("Нет номеров, которые будут свободны к этой дате");
        } else {
            freeRooms.forEach(System.out::println);
        }

        // 9. Услуги гостя с сортировкой
        System.out.println("9. Услуги гостя (сортировка по цене):");
        List<Guest> guests = hotelManager.getGuests();
        if (!guests.isEmpty()) {
            Guest testGuest = guests.get(1);
            List<Service> guestServices = hotelManager.getSortedGuestServices(testGuest, "Цена");
            if (guestServices.isEmpty()) {
                System.out.println("У гостя " + testGuest.getFullName() + " нет услуг");
            } else {
                guestServices.forEach(System.out::println);
            }
        }

        System.out.println("9. Услуги гостя (сортировка по дате):");
        if (!guests.isEmpty()) {
            Guest testGuest = guests.get(1);
            List<Service> guestServices = hotelManager.getSortedGuestServices(testGuest, "Дата");
            if (guestServices.isEmpty()) {
                System.out.println("У гостя " + testGuest.getFullName() + " нет услуг");
            } else {
                guestServices.forEach(System.out::println);
            }
        }

        System.out.println("--- Тестирование функций запросов завершено ---\n");
    }

    private static void testBookingOperations(HotelManager hotelManager) {
        System.out.println("6. ТЕСТИРОВАНИЕ ОПЕРАЦИЙ БРОНИРОВАНИЯ");

        List<Guest> guests = hotelManager.getGuests();
        if (guests.size() < 2) {
            System.out.println("Недостаточно гостей для тестирования бронирования");
            return;
        }

        Guest guest1 = guests.get(0);
        Guest guest2 = guests.get(1);
        List<Guest> bookingGuests = List.of(guest1, guest2);

        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);

        System.out.println("Заселение гостей в номер 101:");
        hotelManager.checkGuestsIn(101, bookingGuests, checkIn, checkOut);

        // 7. Сумма оплаты за номер
        Room room101 = hotelManager.getRoomManager().getRoomByNumber(101).orElse(null);
        if (room101 != null) {
            double totalPrice = hotelManager.getFullRoomPrice(room101);
            System.out.println("7. Сумма оплаты за номер 101: " + totalPrice + " руб.");
        }

        // 8. История проживания (пока пустая, т.к. только заехали)
        System.out.println("8. История номера 101 (последние 3 проживания):");
        hotelManager.showLastThreeGuests(101, 3);

        System.out.println("Свободные номера после заселения:");
        hotelManager.showAllFreeRooms("Цена");
        System.out.println("Общее число свободных номеров: " + hotelManager.countFreeRooms());

        System.out.println("Попытка поставить номер 101 на обслуживание");
        hotelManager.getRoomManager().setRoomMaintenance(101, true);

        System.out.println("Выселение из номера 101:");
        hotelManager.checkGuestsOut(101);

        System.out.println("Установка номера 101 на обслуживание:");
        hotelManager.getRoomManager().setRoomMaintenance(101, true);

        System.out.println("Изменение цены номера 101:");
        hotelManager.getRoomManager().changeRoomPrice(101, 2700.0);

        System.out.println("Финальное состояние номера 101:");
        hotelManager.showFullRoomInfo(101);

        System.out.println("8. История номера 101 после выселения:");
        hotelManager.showLastThreeGuests(101, 3);

        System.out.println("--- Операции бронирования завершены ---\n");
    }

}