package task_6.repository.impl;

import task_6.model.Guest;
import task_6.repository.interfaces.GuestRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий гостей.
 * Хранит данные о гостях в памяти.
 */
public class InMemoryGuestRepository implements GuestRepository {

    private final Map<Long, Guest> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Guest save(Guest guest) {
        if (guest.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(guest, newId);
        }

        storage.put(guest.getId(), guest);
        return guest;
    }

    @Override
    public void delete(Guest guest) {
        storage.remove(guest.getId());
    }

    @Override
    public Optional<Guest> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Guest> findAll() {
        return new ArrayList<>(storage.values());
    }

    private void setId(Guest guest, long id) {
        try {
            Field f = Guest.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(guest, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
