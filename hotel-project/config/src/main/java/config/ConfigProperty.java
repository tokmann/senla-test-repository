package config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки полей, значения которых должны загружаться из конфигурационных файлов.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {

    /**
     * Имя конфигурационного файла.
     * По умолчанию: "application.properties"
     */
    String configFileName() default "application.properties";

    /**
     * Имя свойства в конфигурационном файле.
     * Если не указано, используется имя в формате: SimpleClassName.fieldName
     */
    String propertyName() default "";

    /**
     * Тип конфигурационного значения.
     * По умолчанию: автоматическое определение.
     */
    ConfigType type() default ConfigType.AUTO;
}
