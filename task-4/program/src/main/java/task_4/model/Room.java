package task_4.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Room {

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

    public Room(int number, int capacity, double price, int stars) {
        this.number = number;
        this.capacity = capacity;
        this.price = price;
        this.stars = stars;
        this.isOccupied = false;
        this.underMaintenance = false;
        this.guests = new ArrayList<>();
    }

    public void checkIn(List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        if (!isOccupied && !underMaintenance) {
            this.isOccupied = true;
            this.guests = guests;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            stayHistory.add("Гости(ь): " + guests + " проживали(ют) с " + checkInDate + " по " + checkOutDate);
            if (stayHistory.size() > 10) stayHistory.removeFirst();
        }
    }

    public List<String> getLastStays(int count) {
        int size = stayHistory.size();
        return stayHistory.subList(Math.max(0, size - count), size);
    }

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
        String status = isOccupied ? "Занят (" + guests + ")" : "Свободен";
        String maintenance = underMaintenance ? ", На обслуживании" : "";
        return "Номер " + number + ", Вместимость " + capacity + ", Кол-во звезд: " + stars + ", Цена: " + price + ", Статус: " + status + maintenance;
    }

}