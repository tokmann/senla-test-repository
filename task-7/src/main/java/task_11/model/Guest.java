package task_11.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Модель гостя отеля.
 * Представляет данные о постояльце, включая личную информацию,
 * привязку к номеру и список заказанных услуг.
 */
public class Guest {

    private long id;
    private int age;
    private String firstName;
    private String secondName;
    private Long roomId;
    private Room room;
    private List<Service> services;

    public Guest() {
        this.roomId = null;
    }

    public Guest(long id, int age, String firstName, String secondName, Long roomId) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
        this.roomId = roomId;
    }

    // Полный конструктор
    public Guest(long id, int age, String firstName, String secondName, Long roomId, Room room, List<Service> services) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
        this.roomId = roomId;
        this.room = room;
        this.services = services != null ? new ArrayList<>(services) : new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.roomId = (room != null) ? room.getId() : null;
    }

    public List<Service> getServices() {
        if (services == null) {
            services = new ArrayList<>();
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services != null ? new ArrayList<>(services) : new ArrayList<>();
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    public String getFullInfo() {
        return "ID - " + id + " | Постоялец: Имя - " + firstName + ", Фамилия - " + secondName
                + ", Проживает в номере - " + (room != null ? room.getNumber() : "не заселён");
    }

    @Override
    public String toString() {
        return getFullInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return id == guest.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
