package com.javaquery.spring.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides Liquibase configuration. Sets up the SpringLiquibase bean with the specified changelog file.
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-master.yaml}")
    private String changeLog;

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLog);
        // This is because we are running the process manually. Don't let SpringLiquibase do it.
        liquibase.setShouldRun(false);
        return liquibase;
    }
}
