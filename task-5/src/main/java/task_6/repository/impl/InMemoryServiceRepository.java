package task_6.repository.impl;

import task_6.model.Service;
import task_6.repository.interfaces.ServiceRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий услуг.
 * Позволяет искать услуги по имени и управлять их списком.
 */
public class InMemoryServiceRepository implements ServiceRepository {

    private final Map<Long, Service> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Service save(Service service) {
        if (service.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(service, newId);
        }
        storage.put(service.getId(), service);
        return service;
    }

    @Override
    public void delete(Service service) {
        storage.remove(service.getId());
    }

    @Override
    public Optional<Service> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Service> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Service> findByName(String name) {
        return storage.values().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private void setId(Service s, long id) {
        try {
            Field f = Service.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(s, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
