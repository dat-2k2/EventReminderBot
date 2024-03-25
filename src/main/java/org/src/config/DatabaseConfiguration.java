package org.src.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

public class DatabaseConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    public DatabaseConfiguration(DataSource dataSource, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initializeDatabase(){

    }

    @PostConstruct
    public void registerH2Driver() {
        try {
            Class.forName("org.h2.Driver");
            logger.info("H2 Driver registered successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to register H2 driver", e);
            throw new RuntimeException("Failed to register H2 driver", e);
        }
    }
}
