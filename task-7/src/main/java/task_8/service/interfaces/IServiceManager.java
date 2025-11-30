package task_8.service.interfaces;

import task_8.model.Service;
import task_8.view.enums.ServiceSortOption;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для управления сервисами.
 * Отвечает за добавление, изменение и сортировку услуг.
 */
public interface IServiceManager {

    /**
     * Добавляет новую услугу в систему.
     * Услуга добавляется только если услуги с таким названием еще нет.
     * @param service услуга для добавления
     * @return добавленная услуга или null если услуга с таким названием уже существует
     */
    Service addService(Service service);

    /**
     * Изменяет цену существующей услуги.
     * @param serviceName название услуги
     * @param newPrice новая цена услуги
     */
    void changeServicePrice(String serviceName, double newPrice);

    /**
     * Возвращает список услуг, отсортированный по указанному критерию.
     * @param option критерий сортировки услуг
     * @return отсортированный список услуг
     */
    List<Service> getSortedServices(ServiceSortOption option);

    /**
     * Возвращает все услуги системы.
     * @return список всех услуг
     */
    List<Service> getAllServices();

    /**
     * Ищет услугу по названию (без учёта регистра).
     * Убирает пробелы в начале и конце названия перед поиском.
     * @param name название услуги для поиска
     * @return найденная услуга или null если не найдена
     */
    Service findByName(String name);

    /**
     * Находит услугу по идентификатору.
     * @param id идентификатор услуги
     * @return найденная услуга или null если не найдена
     */
    Optional<Service> getServiceById(long id);

    void syncIdGen();

}