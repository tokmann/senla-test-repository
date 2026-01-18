package task_11.db.interfaces;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    T save(T entity);
    void delete(T entity);
    Optional<T> findById(long id);
    List<T> findAll();
}
