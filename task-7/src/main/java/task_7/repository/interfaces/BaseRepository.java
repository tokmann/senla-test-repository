package task_7.repository.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для всех репозиториев системы.
 * Определяет основные CRUD операции для работы с сущностями.
 * @param <T> тип сущности, с которой работает репозиторий
 */
public interface BaseRepository<T> {

    /**
     * Сохраняет сущность в хранилище.
     * @param entity сущность для сохранения
     * @return сохраненная сущность с установленным ID
     */
    T save(T entity);

    /**
     * Удаляет сущность из хранилища.
     * @param entity сущность для удаления
     */
    void delete(T entity);

    /**
     * Находит сущность по идентификатору.
     * @param id идентификатор сущности
     * @return Optional с найденной сущностью или empty если не найдена
     */
    Optional<T> findById(long id);

    /**
     * Возвращает все сущности из хранилища.
     * @return список всех сущностей
     */
    List<T> findAll();
}
