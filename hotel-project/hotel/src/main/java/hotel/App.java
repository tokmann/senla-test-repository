package hotel;

import hotel.db.EntityManagerFactoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Главный класс консольного приложения отеля.
 * Сканирует и инициализирует все компоненты системы.
 */
@EnableTransactionManagement
@EnableWebMvc
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class App implements WebMvcConfigurer {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactoryProvider emfProvider) {
        return new JpaTransactionManager(emfProvider.getEntityManagerFactory());
    }
}

