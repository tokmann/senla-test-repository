package hotel;

import hotel.db.EntityManagerFactoryProvider;
import hotel.ui.ConsoleUI;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Главный класс консольного приложения отеля.
 * Сканирует и инициализирует все компоненты системы и запускает пользовательский интерфейс.
 */
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class App {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Точка входа в приложение.
     */
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(App.class);

        ConsoleUI ui = context.getBean(ConsoleUI.class);
        ui.run();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EntityManagerFactoryProvider emf =
                    context.getBean(EntityManagerFactoryProvider.class);
            emf.close();
            context.close();
        }));
    }
}

