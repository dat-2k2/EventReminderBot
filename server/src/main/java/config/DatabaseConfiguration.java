package config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * <a href="https://docs.spring.io/spring-data/jpa/reference/repositories/create-instances.html">See</a>
 */
//@Configuration
@Slf4j
@PropertySource("classpath:database.properties")
@EnableJpaRepositories(basePackages = {"repos"})
public class DatabaseConfiguration {
    private static final String entityPackage = "entity";
    @Autowired
    Environment env;

    @Bean
    public DataSource dataSource() {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("driver-class-name"));
        dataSource.setUrl(env.getRequiredProperty("url"));
        dataSource.setUsername(env.getRequiredProperty("username"));
        dataSource.setPassword(env.getRequiredProperty("password"));
        return dataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
//        vendorAdapter.setDatabasePlatform(env.getRequiredProperty("database-platform"));
        vendorAdapter.setDatabase(Database.POSTGRESQL); //can be swapped with the upper
        vendorAdapter.setShowSql(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(entityPackage);
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    /**
     * Using JpaTransactionManager
     * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/hibernate5/HibernateTransactionManager.html">...</a>
     * @return
     */
    @Bean
    public TransactionManager transactionManager() {
        var transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource());

        return transactionManager;
    }
}
