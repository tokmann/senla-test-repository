package task_7.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель гостя отеля.
 * Представляет данные о постояльце, включая личную информацию,
 * привязку к номеру и список заказанных услуг.
 */
public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private int age;
    private final String firstName;
    private final String secondName;
    private transient Room room;
    private transient List<Service> services;

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

    /**
     * Возвращает полное имя гостя в формате "Имя Фамилия".
     * @return полное имя гостя
     */
    public String getFullName() {
        return firstName + " " + secondName;
    }

    /**
     * Возвращает полную информацию о госте в читаемом формате.
     * Включает ID, имя, фамилию и информацию о номере проживания.
     * @return форматированная строка с информацией о госте
     */
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

    public void setGuestServices(List<Service> services) {
        this.services = services != null ? services : new ArrayList<>();
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}
