package task_5.model;

import java.util.List;

/**
 * Модель гостя.
 * Представляет данные о постояльце и его связях (номер, услуги).
 */
public class Guest {

    private int age;
    private final String firstName;
    private final String secondName;
    private Room room;
    private List<Service> services;

    public Guest(int age, String firstName, String secondName, Room room, List<Service> services) {
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
        this.room = room;
        this.services = services;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    public String getFullInfo() {
        return "Постоялец: Имя - " + firstName + ", Фамилия - " + secondName
                + ", Проживает в номере - " + (room != null ? room.getNumber() : "не заселён");
    }

    public Room getGuestRoom() {
        return room;
    }

    public void setGuestRoom(Room room) {
        this.room = room;
    }

    public List<Service> getGuestServices() {
        return services;
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}
