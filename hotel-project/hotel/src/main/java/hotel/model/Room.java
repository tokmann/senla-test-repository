package hotel.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Модель номера в отеле.
 * Хранит данные о состоянии номера, проживающих гостях и истории заселений.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number", unique = true, nullable = false)
    private int number;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "stars", nullable = false)
    private int stars;

    @Column(name = "is_occupied")
    private boolean isOccupied;

    @Column(name = "under_maintenance")
    private boolean underMaintenance;

    @Column(name = "status_change_enabled")
    private boolean statusChangeEnabled;

    @Column(name = "history_size")
    private int historySize;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Guest> guests = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<StayHistory> stayHistory = new ArrayList<>();

    public Room() {
        this.statusChangeEnabled = true;
        this.historySize = 10;
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
        this.statusChangeEnabled = true;
        this.historySize = 10;
        this.guests = new ArrayList<>();
        this.stayHistory = new ArrayList<>();
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
        return stayHistory.stream()
                .map(StayHistory::getEntry)
                .collect(Collectors.toList());
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

        guests.addAll(newGuests);
        this.isOccupied = true;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

        String guestNames = newGuests.stream()
                .map(Guest::getFullName)
                .collect(Collectors.joining(", "));
        String entry = "Гости: " + guestNames + " проживали с " + checkInDate + " по " + checkOutDate;

        stayHistory.add(new StayHistory(this, entry));
        while (stayHistory.size() > historySize) {
            stayHistory.remove(0);
        }

        return true;
    }

    public List<String> getLastStays() {
        return stayHistory.stream()
                .map(StayHistory::getEntry)
                .collect(Collectors.toList());
    }

    public void checkOut() {
        this.isOccupied = false;
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