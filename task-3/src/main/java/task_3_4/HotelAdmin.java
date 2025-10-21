package task_3_4;

import task_3_4.management.HotelManager;

public class HotelAdmin {
    public static void main(String[] args) {
        HotelManager hotelManager = new HotelManager();

        System.out.println("\n=== ДОБАВЛЕНИЕ НОМЕРОВ ===");
        hotelManager.getRoomManager().addRoom(101, "Стандарт", 2500.0);
        hotelManager.getRoomManager().addRoom(102, "Стандарт", 2500.0);
        hotelManager.getRoomManager().addRoom(201, "Люкс", 5000.0);
        hotelManager.getRoomManager().addRoom(202, "Люкс", 5000.0);
        hotelManager.getRoomManager().addRoom(301, "Президентский", 10000.0);

        System.out.println("\n=== ДОБАВЛЕНИЕ УСЛУГ ===");
        hotelManager.getServiceManager().addService("Завтрак", "Континентальный завтрак", 500.0);
        hotelManager.getServiceManager().addService("SPA", "Спа-процедуры", 2000.0);
        hotelManager.getServiceManager().addService("Трансфер", "Трансфер из/в аэропорт", 1500.0);

        System.out.println("\n=== ЗАСЕЛЕНИЕ ГОСТЕЙ ===");
        hotelManager.getRoomManager().checkIn(101, "Иван Иванов");
        hotelManager.getRoomManager().checkIn(201, "Петр Петров");

        System.out.println();
        hotelManager.displayHotelStatus();

        System.out.println("\n=== ИЗМЕНЕНИЕ СТАТУСА НОМЕРА ===");
        hotelManager.getRoomManager().setRoomMaintenance(102, true);

        System.out.println("\n=== ИЗМЕНЕНИЕ ЦЕН НОМЕРА И УСЛУГИ ===");
        hotelManager.getRoomManager().changeRoomPrice(301, 12000.0);
        hotelManager.getServiceManager().changeServicePrice("Завтрак", 600.0);

        System.out.println("\n=== ВЫСЕЛЕНИЕ ГОСТЯ ===");
        hotelManager.getRoomManager().checkOut(101);

        System.out.println("\n=== ФИНАЛЬНЫЙ СТАТУС ГОСТИНИЦЫ ===");
        hotelManager.displayHotelStatus();

        System.out.println("\n=== ТЕСТ ОБРАБОТКИ ОШИБОК ===");
        System.out.print(" * Пробуем добавить гостя в несуществующую комнату: ");
        hotelManager.getRoomManager().checkIn(999, "Тестовый гость");
        System.out.print(" * Пробуем добавить гостя в комнату на ремонте: ");
        hotelManager.getRoomManager().checkIn(102, "Тестовый гость");
        System.out.print(" * Пробуем изменить цену несуществующей услуги: ");
        hotelManager.getServiceManager().changeServicePrice("Несуществующая услуга", 1000.0);
    }
}