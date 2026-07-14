package tennis.score.board.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("tennis.score.board")
    // Здесь можно сканировать только "tennis.score.board.service", "tennis.score.board.repository", "tennis.score.board.web.mapper"
@PropertySource("classpath:hibernate.properties")
@EnableTransactionManagement
public class PersistenceConfiguration {

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    private final Environment env;

    // Если у класса есть ровно один конструктор, Spring автоматически использует его для внедрения зависимостей —
        // даже без @Autowired. Можно удалить конструктор и поставить над классом @RequiredArgsConstructor
    @Autowired
    public PersistenceConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("hibernate.driver_class"));
        dataSource.setUrl(env.getRequiredProperty("hibernate.connection.url"));
        dataSource.setUsername(env.getRequiredProperty("hibernate.connection.username"));
        dataSource.setPassword(env.getRequiredProperty("hibernate.connection.password"));
        return dataSource;
    }

    // Можно внедрять бин DataSource как параметр метода. Spring автоматически найдёт бин типа DataSource
        // (созданный методом dataSource()) и передаст его в entityManagerFactory при вызове.
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(Boolean.parseBoolean(env.getRequiredProperty("hibernate.show_sql")));

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("tennis.score.board.model.entity");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(hibernateProperties());

        return factory;
    }

    // Для устранения дублирования при добавлении каждого опционального свойства
        // можно создать вспомогательный метод addPropertyIfPresent
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));

        String hbm2ddlAuto = env.getProperty("hibernate.hbm2ddl.auto");
        if (hbm2ddlAuto != null) {
            properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        }

        String formatSql = env.getProperty("hibernate.format_sql");
        if (formatSql != null) {
            properties.put("hibernate.format_sql", formatSql);
        }

        properties.put("jakarta.persistence.validation.mode", "none");
        return properties;
    }

    private void addPropertyIfPresent(Properties properties, String key) {
        String value = env.getProperty(key);
        if (value != null) {
            properties.put(key, value);
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
