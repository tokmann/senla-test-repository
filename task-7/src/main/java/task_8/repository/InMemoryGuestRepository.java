package task_8.repository;

import di.Component;
import task_8.model.Guest;
import task_8.repository.interfaces.GuestRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория гостей.
 * Хранит данные в HashMap с автоматической генерацией ID.
 */
@Component
public class InMemoryGuestRepository implements GuestRepository {

    private final Map<Long, Guest> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Сохраняет гостя в хранилище.
     * Если гость новый (id = 0), генерирует уникальный ID.
     * Если гость существует, перезаписывает данные.
     * @param guest гость для сохранения
     * @return сохраненный гость с установленным ID
     */
    @Override
    public Guest save(Guest guest) {
        if (guest.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(guest, newId);
        }

        storage.put(guest.getId(), guest);
        return guest;
    }

    /**
     * Удаляет гостя из хранилища.
     * @param guest гость для удаления
     */
    @Override
    public void delete(Guest guest) {
        storage.remove(guest.getId());
    }

    /**
     * Находит гостя по идентификатору.
     * @param id идентификатор гостя
     * @return Optional с найденным гостем или empty если не найден
     */
    @Override
    public Optional<Guest> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Возвращает всех гостей из хранилища.
     * @return список всех гостей
     */
    @Override
    public List<Guest> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Устанавливает ID гостю через reflection.
     * Используется для новых гостей при сохранении.
     * @param guest гость для установки ID
     * @param id новый идентификатор
     */
    private void setId(Guest guest, long id) {
        try {
            Field f = Guest.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(guest, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void syncIdGen() {
        long maxId = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        idGen.set(maxId + 1);
    }
}
