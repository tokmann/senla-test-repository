package task_8.repository;

import di.Component;
import task_8.model.Service;
import task_8.repository.interfaces.ServiceRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория услуг.
 * Хранит данные в HashMap с поддержкой поиска по названию услуги.
 */
@Component
public class InMemoryServiceRepository implements ServiceRepository {

    private final Map<Long, Service> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Сохраняет услугу в хранилище.
     * Если услуга новая (id = 0), генерирует уникальный ID.
     * @param service услуга для сохранения
     * @return сохраненная услуга с установленным ID
     */
    @Override
    public Service save(Service service) {
        if (service.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(service, newId);
        }
        storage.put(service.getId(), service);
        return service;
    }

    /**
     * Удаляет услугу из хранилища.
     * @param service услуга для удаления
     */
    @Override
    public void delete(Service service) {
        storage.remove(service.getId());
    }

    /**
     * Находит услугу по идентификатору.
     * @param id идентификатор услуги
     * @return Optional с найденной услугой или empty если не найдена
     */
    @Override
    public Optional<Service> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Возвращает все услуги из хранилища.
     * @return список всех услуг
     */
    @Override
    public List<Service> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Находит услугу по названию (без учета регистра).
     * @param name название услуги для поиска
     * @return Optional с найденной услугой или empty если не найдена
     */
    @Override
    public Optional<Service> findByName(String name) {
        return storage.values().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Устанавливает ID услуге через reflection.
     * @param service услуга для установки ID
     * @param id новый идентификатор
     */
    private void setId(Service service, long id) {
        try {
            Field f = Service.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(service, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void syncIdGen() {
        long maxId = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        idGen.set(maxId + 1);
    }
}
