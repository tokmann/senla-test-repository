package task_6.model;

import java.util.List;

/**
 * Модель гостя.
 * Представляет данные о постояльце и его связях (номер, услуги).
 */
public class Guest {

    private long id;
    private int age;
    private final String firstName;
    private final String secondName;
    private Room room;
    private List<Service> services;

    public Guest(long id, int age, String firstName, String secondName, Room room, List<Service> services) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
        this.room = room;
        this.services = services;
    }

    public long getId() {
        return id;
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
        return "ID - " + id + " | Постоялец: Имя - " + firstName + ", Фамилия - " + secondName
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

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}
