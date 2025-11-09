package task_5.model.repository;

import task_5.model.Guest;

import java.util.ArrayList;
import java.util.List;

public class GuestRepository {

    private final List<Guest> guests = new ArrayList<>();

    public void save(Guest guest) {
        guests.add(guest);
    }

    public void delete(Guest guest) {
        guests.remove(guest);
    }

    public List<Guest> findAll() {
        return new ArrayList<>(guests);
    }
}
