package task_5.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель номера в отеле.
 * Хранит данные о состоянии, проживающих и истории заселений.
 */
public class Room {

    private long id;
    private final int number;
    private final int capacity;
    private double price; // Цена за сутки
    private final int stars;
    private boolean isOccupied;
    private boolean underMaintenance;
    private List<Guest> guests;
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

    public List<Guest> getGuests() {
        return guests;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public String toString() {
        String status = isOccupied ? "Занят (" + guests + ") с " + getCheckInDate() + " по " + getCheckOutDate() : "Свободен";

        String maintenance = underMaintenance ? ", На обслуживании" : "";
        return "Номер " + number + ", Вместимость " + capacity + ", Кол-во звезд: " + stars + ", Цена: " + price + ", Статус: " + status + maintenance;
    }

    /**
     * Заселение гостей.
     * Проверяет вместимость и статус обслуживания.
     */
    public boolean checkIn(List<Guest> newGuests, LocalDate checkInDate, LocalDate checkOutDate) {
        if (underMaintenance) return false;
        int availableSpots = capacity - guests.size();
        if (newGuests.size() > availableSpots) return false;

        guests.addAll(newGuests);
        this.isOccupied = true;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

        // Логирование истории заселения
        String guestNames = newGuests.stream()
                .map(g -> g.getFirstName() + " " + g.getSecondName())
                .toList()
                .toString();
        stayHistory.add("Гости: " + guestNames + " проживали с " + checkInDate + " по " + checkOutDate);
        if (stayHistory.size() > 10) stayHistory.removeFirst();
        return true;
    }

    /** Получение истории жителей комнаты */
    public List<String> getLastStays(int count) {
        int size = stayHistory.size();
        return stayHistory.subList(Math.max(0, size - count), size);
    }

    /** Выселение всех гостей */
    public void checkOut() {
        this.isOccupied = false;
        this.guests = new ArrayList<>();
        clearOccupationTime();
    }

    public void clearOccupationTime() {
        this.checkInDate = null;
        this.checkOutDate = null;
    }

    public void setMaintenance(boolean maintenance) {
        if (!isOccupied) {
            this.underMaintenance = maintenance;
        }
    }



}