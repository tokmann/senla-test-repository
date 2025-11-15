package task_6.model;

import java.time.LocalDate;

/**
 * Модель дополнительной услуги отеля.
 */
public class Service {

    private long id;
    private String name;
    private String description;
    private double price;
    private LocalDate date;

    public Service(long id, String name, String description, double price, LocalDate date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.date = date;
    }

    public long getId() {
        return id;
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

    @Override
    public String toString() {
        return "Услуга: " + name + ", Описание: " + description + ", Цена: " + price + ", Дата: " + date;
    }
}