package config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class AnnotationConfigurationLoader {

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
                throw new RuntimeException("Файл-конфиг не найден: " + fileName, e);
            }

            String rawValue = props.getProperty(propertyName);
            if (rawValue == null) continue;

            field.setAccessible(true);

            try {
                Object converted = convertValue(rawValue, field.getType(), annotation.type());
                field.set(target, converted);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось установить значение из конфига: " + propertyName, e);
            }
        }
    }

    private static Object convertValue(String value, Class<?> fieldType, ConfigType type) {
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

        return value;
    }
}
