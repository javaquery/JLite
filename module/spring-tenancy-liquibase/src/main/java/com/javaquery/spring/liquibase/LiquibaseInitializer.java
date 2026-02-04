package com.javaquery.spring.liquibase;

import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

/**
 * Initializer that triggers Liquibase execution on application startup.
 * This class implements CommandLineRunner to ensure automatic initialization
 * without requiring explicit autowiring of LiquibaseService.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Slf4j
public class LiquibaseInitializer implements CommandLineRunner {

    private final LiquibaseService liquibaseService;

    /**
     * Constructor for LiquibaseInitializer.
     *
     * @param liquibaseService the LiquibaseService to initialize
     */
    public LiquibaseInitializer(LiquibaseService liquibaseService) {
        this.liquibaseService = liquibaseService;
    }

    /**
     * Executes Liquibase initialization after the application context is fully loaded.
     * This method is automatically called by Spring Boot's CommandLineRunner mechanism.
     *
     * @param args application arguments (not used)
     * @throws LiquibaseException if Liquibase execution fails
     */
    @Override
    public void run(String... args) throws LiquibaseException {
        log.debug("LiquibaseInitializer: Starting Liquibase initialization");
        liquibaseService.initialize();
        log.debug("LiquibaseInitializer: Liquibase initialization completed");
    }
}
