package hotel.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Модель гостя отеля.
 * Представляет данные о постояльце, включая личную информацию,
 * привязку к номеру и список заказанных услуг.
 */
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "second_name", nullable = false, length = 50)
    private String secondName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToMany
    @JoinTable(
            name = "guest_services",
            joinColumns = @JoinColumn(name = "guest_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services = new ArrayList<>();

    public Guest() {
    }

    public Guest(long id, int age, String firstName, String secondName) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public Guest(long id, int age, String firstName, String secondName, Room room, List<Service> services) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.secondName = secondName;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
