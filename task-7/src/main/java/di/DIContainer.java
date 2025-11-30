package di;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {

    private final Map<Class<?>, Class<?>> implRegistry = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    public void register(Class<?> iface, Class<?> impl) {
        implRegistry.put(iface, impl);
    }

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

    private Class<?> resolveImplementation(Class<?> type) {
        if (implRegistry.containsKey(type)) return implRegistry.get(type);
        if (!type.isInterface()) return type;
        throw new RuntimeException("Нет имплементации для " + type.getName());
    }

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
