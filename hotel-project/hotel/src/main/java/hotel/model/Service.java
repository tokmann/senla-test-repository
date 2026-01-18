package hotel.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель дополнительной услуги отеля.
 */
public class Service {

    private long id;
    private String name;
    private String description;
    private double price;
    private LocalDate date;
    private long guestId;

    public Service() {
    }

    public Service(long id, String name, String description, double price, LocalDate date, long guestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.date = date;
        this.guestId = guestId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getGuestId() {
        return guestId;
    }

    public void setGuestId(long guestId) {
        this.guestId = guestId;
    }

    @Override
    public String toString() {
        return "Услуга: " + name + ", Описание: " + description + ", Цена: " + price + ", Дата: " + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return id == service.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}