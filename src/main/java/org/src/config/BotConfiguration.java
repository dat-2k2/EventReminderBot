package org.src.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Class to retrieve private data from resource.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class BotConfiguration {

//    BOT INFO
    @Value("${bot.name}")
    String botName;

    @Value("${bot.key")
    String token;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        return dataSource;
    }

    @Bean
    public DatabaseConfiguration databaseConfig(DataSource dataSource, ResourceLoader resourceLoader) {
        return new DatabaseConfiguration(dataSource, resourceLoader);
    }


    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }
}
