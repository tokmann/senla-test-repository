package hotel.db.interfaces;

import java.util.List;

public interface BaseRepository<T> {
    T save(T entity);
    void delete(T entity);
    T findById(long id);
    List<T> findAll();
}
