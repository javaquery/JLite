package com.javaquery.spring.liquibase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Liquibase configuration properties.
 * @author vicky.thakor
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.datasource.liquibase")
@Getter
@Setter
public class LiquibaseProperties {
    private boolean initializeOnStartup = false;
    private int maximumPoolSize = 2;
    private int minimumIdle = 1;
    private long connectionTimeout = 30000;
    private long idleTimeout = 300000;
    private long maxLifetime = 900000;
    private long leakDetectionThreshold = 60000;
    private long validationTimeout = 5000;
}
