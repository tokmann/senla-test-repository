package task_11.repository.interfaces;

import task_11.model.Service;

import java.util.Optional;

/**
 * Репозиторий для работы с услугами отеля.
 * Предоставляет метод для поиска услуг.
 */
public interface ServiceRepository extends BaseRepository<Service> {

    /**
     * Находит услугу по названию (без учета регистра).
     * @param name название услуги для поиска
     * @return Optional с найденной услугой или empty если не найдена
     */
    Optional<Service> findByName(String name);
}