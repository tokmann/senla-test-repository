package task_7.model;

import task_7.config.ConfigManager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель номера в отеле.
 * Хранит данные о состоянии номера, проживающих гостях и истории заселений.
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private final int number;
    private final int capacity;
    private double price; // Цена за сутки
    private final int stars;
    private boolean isOccupied;
    private boolean underMaintenance;
    private transient List<Guest> guests;
    private final List<String> stayHistory = new ArrayList<>();
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Room(long id, int number, int capacity, double price, int stars) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.price = price;
        this.stars = stars;
        this.isOccupied = false;
        this.underMaintenance = false;
        this.guests = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public List<Guest> getGuests() {
        if (guests == null) {
            guests = new ArrayList<>();
        }
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests != null ? guests : new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStars() {
        return stars;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Возвращает строковое представление номера.
     * Включает ID, номер, вместимость, категорию, цену, статус и обслуживание.
     * @return форматированная строка с информацией о номере
     */
    @Override
    public String toString() {
        String status = isOccupied ? "Занят (" + guests + ") с " + getCheckInDate() + " по " + getCheckOutDate() : "Свободен";

        String maintenance = underMaintenance ? ", На обслуживании" : "";
        return "ID - " + id + " | Номер " + number + ", Вместимость " + capacity + ", Кол-во звезд: " + stars + ", Цена: " + price + ", Статус: " + status + maintenance;
    }

    /**
     * Заселяет гостей в номер на указанный период.
     * Проверяет доступность номера и достаточную вместимость.
     * Обновляет историю проживания.
     * @param newGuests список гостей для заселения
     * @param checkInDate дата заселения
     * @param checkOutDate дата выселения
     * @return true если заселение успешно, false если номер недоступен
     */
    public boolean checkIn(List<Guest> newGuests, LocalDate checkInDate, LocalDate checkOutDate) {
        if (underMaintenance) return false;
        int availableSpots = capacity - guests.size();
        if (newGuests.size() > availableSpots) return false;

        guests.addAll(newGuests);
        this.isOccupied = true;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

        String guestNames = newGuests.stream()
                .map(g -> g.getFirstName() + " " + g.getSecondName())
                .toList()
                .toString();
        stayHistory.add("Гости: " + guestNames + " проживали с " + checkInDate + " по " + checkOutDate);

        int maxSize = ConfigManager.getInstance().getRoomHistorySize();
        while (stayHistory.size() > maxSize) {
            stayHistory.remove(0);
        }
        return true;
    }

    /**
     * Возвращает последние записи из истории проживания.
     * @param count количество последних записей для возврата
     * @return список строк с историей проживания
     */
    public List<String> getLastStays(int count) {
        int size = stayHistory.size();
        return stayHistory.subList(Math.max(0, size - count), size);
    }

    /**
     * Выселяет всех гостей из номера.
     * Очищает список гостей и сбрасывает даты проживания.
     */
    public void checkOut() {
        this.isOccupied = false;
        this.guests = new ArrayList<>();
        clearOccupationTime();
    }

    /**
     * Сбрасывает даты заселения и выселения.
     */
    public void clearOccupationTime() {
        this.checkInDate = null;
        this.checkOutDate = null;
    }

    /**
     * Устанавливает статус обслуживания номера.
     * Номер можно перевести на обслуживание только если он свободен.
     * @param maintenance true для установки на обслуживание, false для снятия
     */
    public void setMaintenance(boolean maintenance) {
        if (!ConfigManager.getInstance().isRoomStatusChangeEnabled()) {
            throw new IllegalStateException("Изменение статуса номера отключено в конфигурации");
        }

        if (!isOccupied) {
            this.underMaintenance = maintenance;
            System.out.println("Обслуживание комнаты: " + maintenance);
        } else {
            System.out.println("Комната занята");
        }
    }
}