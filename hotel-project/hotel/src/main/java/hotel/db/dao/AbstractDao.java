package hotel.db.dao;

import hotel.db.interfaces.BaseRepository;

/**
 * Абстрактный класс DAO для сущностей с базовыми CRUD операциями.
 * Предоставляет реализацию интерфейса BaseRepository для работы с конкретными типами сущностей.
 * @param <T> тип сущности, с которой работает DAO
 */
public abstract class AbstractDao<T> extends BaseDao implements BaseRepository<T> {

}
