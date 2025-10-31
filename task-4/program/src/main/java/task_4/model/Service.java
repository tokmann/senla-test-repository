package task_4.model;

import java.time.LocalDate;

public class Service {

    private final String name;
    private String description;
    private double price;
    private LocalDate date;

    public Service(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
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