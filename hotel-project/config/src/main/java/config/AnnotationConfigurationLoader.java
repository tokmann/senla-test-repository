package config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Загрузчик конфигураций на основе аннотаций.
 * Позволяет автоматически загружать значения полей из конфигурационных файлов.
 */
public class AnnotationConfigurationLoader {

    /**
     * Загружает конфигурацию для целевого объекта.
     * Считывает значения из конфигурационного файла и устанавливает их в поля,
     * помеченные аннотацией @ConfigProperty.
     * @param target объект, для которого загружается конфигурация
     * @throws RuntimeException если файл конфигурации не найден или произошла ошибка при чтении
     */
    public static void configure(Object target) {
        Class<?> clazz = target.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
            if (annotation == null) continue;

            String fileName = annotation.configFileName();
            String propertyName = annotation.propertyName();

            if (propertyName.isEmpty()) {
                propertyName = clazz.getSimpleName() + "." + field.getName();
            }

            Properties props = new Properties();
            try (InputStream is = AnnotationConfigurationLoader.class.getClassLoader().getResourceAsStream(fileName)) {
                if (is == null) throw new RuntimeException("Файл-конфиг не найден: " + fileName);
                props.load(is);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка чтения файла конфига: " + fileName, e);
            }

            String rawValue = props.getProperty(propertyName);
            if (rawValue == null) continue;

            field.setAccessible(true);

            try {
                Object converted = convertValue(rawValue, field);
                field.set(target, converted);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось установить значение из конфига: " + propertyName, e);
            }
        }
    }

    /**
     * Преобразует строковое значение в объект требуемого типа.
     * Поддерживает примитивные типы, массивы, коллекции и объекты с конструктором String.
     * @param value строковое значение из конфигурации
     * @param field поле, в которое должно быть установлено значение
     * @return преобразованное значение
     * @throws Exception если преобразование не удалось
     */
    private static Object convertValue(String value, Field field) throws Exception {
        ConfigType type = field.getAnnotation(ConfigProperty.class).type();
        Class<?> fieldType = field.getType();

        if (type != ConfigType.AUTO) {
            return switch (type) {
                case STRING -> value;
                case INTEGER -> Integer.parseInt(value);
                case BOOLEAN -> Boolean.parseBoolean(value);
                case LONG -> Long.parseLong(value);
                case DOUBLE -> Double.parseDouble(value);
                default -> value;
            };
        }

        if (fieldType == int.class || fieldType == Integer.class) return Integer.parseInt(value);
        if (fieldType == boolean.class || fieldType == Boolean.class) return Boolean.parseBoolean(value);
        if (fieldType == long.class || fieldType == Long.class) return Long.parseLong(value);
        if (fieldType == double.class || fieldType == Double.class) return Double.parseDouble(value);
        if (fieldType == String.class) return value;

        if (fieldType.isArray()) {
            return convertArray(value, fieldType.getComponentType());
        }

        if (Collection.class.isAssignableFrom(fieldType)) {
            return convertCollection(value, field);
        }

        try {
            Constructor<?> ctor = fieldType.getConstructor(String.class);
            return ctor.newInstance(value);
        } catch (NoSuchMethodException ignored) {
            throw new RuntimeException(
                    "Не удалось создать объект типа " + fieldType.getName() +
                            ": нет конструктора (String)"
            );
        }
    }

    /**
     * Преобразует строку в массив значений.
     * @param value строковое представление массива (значения разделены запятыми)
     * @param elementType тип элементов массива
     * @return массив преобразованных значений
     */
    private static Object convertArray(String value, Class<?> elementType) {
        String[] parts = value.split(",");
        Object array = Array.newInstance(elementType, parts.length);

        for (int i = 0; i < parts.length; i++) {
            Array.set(array, i, convertSingle(parts[i].trim(), elementType));
        }

        return array;
    }

    /**
     * Преобразует строку в коллекцию значений.
     * Поддерживает List и Set.
     * @param value строковое представление коллекции (значения разделены запятыми)
     * @param field поле коллекции
     * @return коллекция преобразованных значений
     */
    private static Object convertCollection(String value, Field field) {
        String[] parts = value.split(",");
        Collection<Object> collection;

        if (field.getType() == Set.class) {
            collection = new HashSet<>();
        } else {
            collection = new ArrayList<>();
        }

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType pt) {
            Class<?> elementType = (Class<?>) pt.getActualTypeArguments()[0];

            for (String part : parts) {
                collection.add(convertSingle(part.trim(), elementType));
            }
        }

        return collection;
    }

    /**
     * Преобразует одиночное строковое значение в указанный тип.
     * @param raw строковое значение
     * @param type целевой тип
     * @return преобразованное значение
     */
    private static Object convertSingle(String raw, Class<?> type) {
        if (type == Integer.class || type == int.class) return Integer.parseInt(raw);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(raw);
        if (type == Long.class || type == long.class) return Long.parseLong(raw);
        if (type == Double.class || type == double.class) return Double.parseDouble(raw);
        return raw;
    }
}
