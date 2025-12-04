package di;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Контейнер внедрения зависимостей (DI Container).
 * Предоставляет механизмы для регистрации зависимостей и управления жизненным циклом бинов.
 */
public class DIContainer {

    /** Реестр сопоставлений интерфейсов с их реализациями */
    private final Map<Class<?>, Class<?>> implRegistry = new HashMap<>();

    /** Кэш синглтон-объектов */
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    /**
     * Регистрирует сопоставление интерфейса с его реализацией.
     * @param iface интерфейс или абстрактный класс
     * @param impl конкретный класс, реализующий интерфейс
     */
    public void register(Class<?> iface, Class<?> impl) {
        implRegistry.put(iface, impl);
    }

    /**
     * Получает экземпляр бина указанного типа.
     * Если бин уже существует как синглтон, возвращает существующий экземпляр.
     * В противном случае создает новый экземпляр с внедрением зависимостей.
     * @param <T> тип возвращаемого бина
     * @param type класс запрашиваемого типа
     * @return экземпляр бина указанного типа
     * @throws RuntimeException если не удалось создать или найти бин
     */
    public <T> T getBean(Class<T> type) {
        try {
            Object bean = singletons.get(type);
            if (bean != null) return type.cast(bean);

            Class<?> impl = resolveImplementation(type);
            return type.cast(createBean(impl));
        } catch (Exception e) {
            throw new RuntimeException("Не найден бин для " + type.getName(), e);
        }
    }

    /**
     * Определяет конкретную реализацию для запрашиваемого типа.
     * @param type запрашиваемый тип (интерфейс или класс)
     * @return конкретный класс реализации
     * @throws RuntimeException если для интерфейса не найдена реализация
     */
    private Class<?> resolveImplementation(Class<?> type) {
        if (implRegistry.containsKey(type)) return implRegistry.get(type);
        if (!type.isInterface()) return type;
        throw new RuntimeException("Нет имплементации для " + type.getName());
    }

    /**
     * Создает экземпляр бина с внедрением зависимостей.
     * Обрабатывает поля, помеченные аннотацией @Inject.
     * @param impl класс создаваемого бина
     * @return созданный экземпляр бина
     * @throws Exception если произошла ошибка при создании экземпляра или внедрении зависимостей
     */
    private Object createBean(Class<?> impl) throws Exception {
        Object existing = singletons.get(impl);
        if (existing != null) return existing;

        if (!impl.isAnnotationPresent(Component.class)) {
            throw new RuntimeException("Класс " + impl.getName() + " не помечен @Component");
        }

        Object instance = impl.getDeclaredConstructor().newInstance();
        singletons.put(impl, instance);

        for (Field field : impl.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldType = field.getType();
                Object dep = getBean(fieldType);
                field.setAccessible(true);
                field.set(instance, dep);
            }
        }

        return instance;
    }

}
