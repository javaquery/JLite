package com.javaquery.spring.liquibase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * Provides Liquibase service to initialize database schemas for multiple tenants.
 * @author vicky.thakor
 * @since 1.0.0
 */
@Service
@Slf4j
@DependsOn("liquibase")
public class LiquibaseService {

    @Autowired
    private LiquibaseProperties liquibaseProperties;

    @Autowired
    @Qualifier("liquibase")
    private SpringLiquibase springLiquibase;

    @Autowired
    private LiquibaseDataSource liquibaseDataSource;

    @PostConstruct
    public void initialize() throws LiquibaseException {
        log.info("Initializing Liquibase Service with properties: {}", liquibaseProperties);
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
     * Manually initialize schema for a specific tenant.
     *
     * @param tenantDataSource The tenant-specific data source configuration.
     */
    public void initSchemaForTenant(TenantDataSource tenantDataSource) throws LiquibaseException {
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
        }
        return null;
    }
}
