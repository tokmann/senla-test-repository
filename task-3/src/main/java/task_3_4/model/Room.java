package task_3_4.model;

public class Room {

    private final int number;
    private final String type;
    private double price;
    private boolean isOccupied;
    private boolean underMaintenance;
    private String guestName;

    public Room(int number, String type, double price) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.isOccupied = false;
        this.underMaintenance = false;
        this.guestName = "";
    }

    public void checkIn(String guestName) {
        if (!isOccupied && !underMaintenance) {
            this.isOccupied = true;
            this.guestName = guestName;
        }
    }

    public void checkOut() {
        this.isOccupied = false;
        this.guestName = "";
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

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public String getGuestName() {
        return guestName;
    }

    @Override
    public String toString() {
        String status = isOccupied ? "Занят (" + guestName + ")" : "Свободен";
        String maintenance = underMaintenance ? ", На обслуживании" : "";
        return "Номер " + number + ": " + type + ", Цена: " + price + ", Статус: " + status + maintenance;
    }
}