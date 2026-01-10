package task_11.model;

import config.AnnotationConfigurationLoader;
import config.ConfigProperty;
import config.ConfigType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Модель номера в отеле.
 * Хранит данные о состоянии номера, проживающих гостях и истории заселений.
 */
public class Room {

    private long id;
    private int number;
    private int capacity;
    private double price;
    private int stars;
    private boolean isOccupied;
    private boolean underMaintenance;

    @ConfigProperty(propertyName = "room.status.change.enabled", type = ConfigType.BOOLEAN)
    private boolean statusChangeEnabled;

    private List<Guest> guests;
    private List<String> stayHistory;

    @ConfigProperty(propertyName = "room.history.size", type = ConfigType.INTEGER)
    private int historySize;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // No-arg constructor
    public Room() {
        this.guests = new ArrayList<>();
        this.stayHistory = new ArrayList<>();
    }

    public Room(long id, int number, int capacity, double price, int stars) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.price = price;
        this.stars = stars;
        this.isOccupied = false;
        this.underMaintenance = false;
        this.guests = new ArrayList<>();
        this.stayHistory = new ArrayList<>();
        AnnotationConfigurationLoader.configure(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public void setStars(int stars) {
        this.stars = stars;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public boolean isStatusChangeEnabled() {
        return statusChangeEnabled;
    }

    public void setStatusChangeEnabled(boolean statusChangeEnabled) {
        this.statusChangeEnabled = statusChangeEnabled;
    }

    public List<Guest> getGuests() {
        if (guests == null) {
            guests = new ArrayList<>();
        }
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests != null ? new ArrayList<>(guests) : new ArrayList<>();
    }

    public List<String> getStayHistory() {
        return new ArrayList<>(stayHistory); // Defensive copy
    }

    public void setStayHistory(List<String> stayHistory) {
        this.stayHistory = stayHistory != null ? new ArrayList<>(stayHistory) : new ArrayList<>();
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        String status = isOccupied ? "Занят (" + guests.size() + " гостей) с " + checkInDate + " по " + checkOutDate : "Свободен";
        String maintenance = underMaintenance ? ", На обслуживании" : "";
        return "ID - " + id + " | Номер " + number + ", Вместимость " + capacity + ", Кол-во звезд: " + stars + ", Цена: " + price + ", Статус: " + status + maintenance;
    }

    public boolean checkIn(List<Guest> newGuests, LocalDate checkInDate, LocalDate checkOutDate) {
        if (underMaintenance) return false;
        int availableSpots = capacity - guests.size();
        if (newGuests.size() > availableSpots) return false;
        for (Guest guest : newGuests) {
            guest.setRoomId(this.id);
            guest.setRoom(this);
        }
        guests.addAll(newGuests);
        this.isOccupied = true;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        String guestNames = newGuests.stream()
                .map(Guest::getFullName)
                .collect(Collectors.joining(", "));
        stayHistory.add("Гости: " + guestNames + " проживали с " + checkInDate + " по " + checkOutDate);
        while (stayHistory.size() > historySize) {
            stayHistory.remove(0);
        }
        return true;
    }

    public List<String> getLastStays() {
        return new ArrayList<>(stayHistory);
    }

    public void checkOut() {
        this.isOccupied = false;
        for (Guest guest : guests) {
            guest.setRoomId(null);
            guest.setRoom(null);
        }
        this.guests.clear();
        clearOccupationTime();
    }

    public void clearOccupationTime() {
        this.checkInDate = null;
        this.checkOutDate = null;
    }

    public void setMaintenance(boolean maintenance) {
        if (!statusChangeEnabled) {
            throw new IllegalStateException("Изменение статуса номера отключено в конфигурации");
        }
        if (!isOccupied) {
            this.underMaintenance = maintenance;
            System.out.println("Обслуживание комнаты: " + maintenance);
        } else {
            System.out.println("Комната занята");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}