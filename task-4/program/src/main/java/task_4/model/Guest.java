package task_4.model;

import java.util.List;

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

    public String getFullName() {
        return "Постоялец: Имя - " + firstName + " Фамилия - " + secondName;
    }

    public String getFullInfo() {
        return "Постоялец: Имя - " + firstName + " Фамилия - " + secondName + "\n"
                + "Проживает в номере - " + room;
    }

    public Room getGuestRoom() {
        return room;
    }

    public List<Service> getGuestServices() {
        return services;
    }

    @Override
    public String toString() {
        return "Постоялец: Имя - " + firstName + ", Фамилия - " + secondName;
    }
}
