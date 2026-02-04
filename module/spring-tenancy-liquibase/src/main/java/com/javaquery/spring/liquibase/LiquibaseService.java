package com.javaquery.spring.liquibase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides Liquibase service to initialize database schemas for multiple tenants.
 * <p>
 * This service is automatically configured by Spring Boot's auto-configuration and can be used
 * in two ways:
 * <ul>
 *   <li><b>Automatic Initialization:</b> Set {@code spring.datasource.liquibase.initialize-on-startup=true}
 *       in your application properties. The {@link LiquibaseInitializer} will automatically trigger
 *       initialization on application startup without requiring explicit autowiring.</li>
 *   <li><b>Manual Initialization:</b> Inject this service into your components and call
 *       {@link #runLiquibaseForTenant(TenantDataSource)} or {@link #initialize()} methods as needed
 *       for on-demand tenant provisioning.</li>
 * </ul>
 *
 * @author vicky.thakor
 * @since 1.0.0
 * @see LiquibaseInitializer
 */
@Slf4j
public class LiquibaseService {

    private final LiquibaseProperties liquibaseProperties;
    private final SpringLiquibase springLiquibase;
    private final LiquibaseDataSource liquibaseDataSource;

    /**
     * Constructor for LiquibaseService.
     *
     * @param liquibaseProperties the Liquibase configuration properties
     * @param springLiquibase the SpringLiquibase bean
     * @param liquibaseDataSource the tenant data source provider
     */
    public LiquibaseService(
            LiquibaseProperties liquibaseProperties,
            SpringLiquibase springLiquibase,
            LiquibaseDataSource liquibaseDataSource) {
        this.liquibaseProperties = liquibaseProperties;
        this.springLiquibase = springLiquibase;
        this.liquibaseDataSource = liquibaseDataSource;
    }

    /**
     * Initializes Liquibase for all tenant data sources on application startup.
     * This method will run Liquibase against each tenant's data source if the
     * 'spring.datasource.liquibase.initialize-on-startup' property is set to true.
     * <p>
     * <b>Note:</b> This method is automatically called by {@link LiquibaseInitializer} during
     * application startup when automatic initialization is enabled. You don't need to call it
     * manually unless you're implementing custom initialization logic.
     *
     * @throws LiquibaseException if Liquibase execution fails for any tenant
     * @see LiquibaseInitializer
     */
    public void initialize() throws LiquibaseException {
        if (liquibaseProperties.isInitializeOnStartup()) {
            int successCount = 0;
            int failureCount = 0;
            List<TenantDataSource> tenantDataSources = liquibaseDataSource.getAllTenants();
            if (tenantDataSources != null && !tenantDataSources.isEmpty()) {
                for (TenantDataSource tenantDataSource : tenantDataSources) {
                    DataSource dataSource = hikariDataSource(tenantDataSource);
                    try {
                        runLiquibaseForDataSource(dataSource);
                        successCount++;
                    } catch (Exception e) {
                        failureCount++;
                        log.error(
                                "Liquibase execution failed for datasource: {}. Error: {}", dataSource, e.getMessage());
                    }
                }
                log.info("Liquibase execution completed. Success: {}, Failures: {}", successCount, failureCount);
                if (failureCount > 0) {
                    throw new LiquibaseException(String.format(
                            "Liquibase execution failed for %d out of %d tenants",
                            failureCount, tenantDataSources.size()));
                }
            }
        } else {
            log.info("Liquibase startup run is disabled.");
        }
    }

    /**
     * Manually trigger liquibase execution for a specific tenant data source.
     *
     * @param tenantDataSource The tenant-specific data source configuration.
     * @throws LiquibaseException if Liquibase execution fails for the tenant
     */
    public void runLiquibaseForTenant(TenantDataSource tenantDataSource) throws LiquibaseException {
        try (HikariDataSource dataSource = hikariDataSource(tenantDataSource)) {
            runLiquibaseForDataSource(dataSource);
            log.info("Liquibase schema has been initialized.");
        }
    }

    /**
     * Create a new SpringLiquibase instance for the given DataSource.
     *
     * @param dataSource The DataSource to run Liquibase against.
     */
    private void runLiquibaseForDataSource(DataSource dataSource) throws LiquibaseException {
        try (Connection connection = dataSource.getConnection()) {
            // Test connection first
            if (connection.isClosed()) {
                throw new SQLException("Connection is closed");
            }
            // Create isolated SpringLiquibase instance
            SpringLiquibase tenantLiquibase = createTenantLiquibase(dataSource);
            tenantLiquibase.afterPropertiesSet();
        } catch (SQLException e) {
            log.error("Database connection error during Liquibase execution", e);
            throw new LiquibaseException("Database connection failed", e);
        } catch (Exception e) {
            log.error("Liquibase execution failed", e);
            throw new LiquibaseException("Liquibase execution failed", e);
        }
    }

    /**
     * Create a new SpringLiquibase instance for the given DataSource.
     *
     * @param dataSource The DataSource to run Liquibase against.
     * @return A configured SpringLiquibase instance.
     */
    private SpringLiquibase createTenantLiquibase(DataSource dataSource) {
        SpringLiquibase tenantLiquibase = new SpringLiquibase();
        tenantLiquibase.setDataSource(dataSource);
        tenantLiquibase.setChangeLog(springLiquibase.getChangeLog());
        tenantLiquibase.setContexts(springLiquibase.getContexts());
        tenantLiquibase.setDefaultSchema(springLiquibase.getDefaultSchema());
        tenantLiquibase.setDropFirst(springLiquibase.isDropFirst());
        tenantLiquibase.setResourceLoader(springLiquibase.getResourceLoader());
        tenantLiquibase.setShouldRun(true);
        return tenantLiquibase;
    }

    /**
     * Create HikariDataSource for the given TenantDataSource.
     *
     * @param tenantDataSource The tenant-specific data source configuration.
     * @return A configured HikariDataSource.
     */
    private HikariDataSource hikariDataSource(TenantDataSource tenantDataSource) {
        if (tenantDataSource != null) {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(tenantDataSource.getDriver());
            config.setJdbcUrl(tenantDataSource.connectionURL());
            config.setUsername(tenantDataSource.getUsername());
            config.setPassword(tenantDataSource.getPassword());

            // Configure connection pool for tenant
            config.setMaximumPoolSize(liquibaseProperties.getMaximumPoolSize());
            config.setMinimumIdle(liquibaseProperties.getMinimumIdle());
            config.setConnectionTimeout(liquibaseProperties.getConnectionTimeout());
            config.setIdleTimeout(liquibaseProperties.getIdleTimeout());
            config.setMaxLifetime(liquibaseProperties.getMaxLifetime());
            config.setLeakDetectionThreshold(liquibaseProperties.getLeakDetectionThreshold());

            String tenantName = tenantDataSource.getTenantName() != null
                            && !tenantDataSource.getTenantName().isEmpty()
                    ? tenantDataSource.getTenantName()
                    : UUID.randomUUID().toString();
            String poolName = "tenant-" + tenantName + "-liquibase-pool";
            config.setPoolName(poolName);

            // Connection validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(liquibaseProperties.getValidationTimeout());

            return new HikariDataSource(config);
        }
        return null;
    }
}
