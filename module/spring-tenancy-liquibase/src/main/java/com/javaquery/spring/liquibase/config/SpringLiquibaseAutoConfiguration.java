package com.javaquery.spring.liquibase.config;

import com.javaquery.spring.liquibase.LiquibaseDataSource;
import com.javaquery.spring.liquibase.LiquibaseProperties;
import com.javaquery.spring.liquibase.LiquibaseService;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * Auto-configuration for Liquibase multi-tenancy support.
 * This configuration will automatically be picked up by Spring Boot when the module is on the classpath.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@AutoConfiguration(after = LiquibaseAutoConfiguration.class)
@ConditionalOnClass({SpringLiquibase.class, LiquibaseDataSource.class})
@EnableConfigurationProperties(LiquibaseProperties.class)
public class SpringLiquibaseAutoConfiguration {

    @Value("${spring.liquibase.change-log:\"classpath:db/changelog/db.changelog-master.yaml\"}")
    private String changeLog;

    /**
     * Creates a SpringLiquibase bean if one doesn't already exist.
     * This bean is configured with the changelog but set not to run automatically,
     * as the LiquibaseService will handle the execution for each tenant.
     *
     * @param resourceLoader the ResourceLoader to use for resolving changelog resources
     * @return SpringLiquibase instance
     */
    @Bean
    @ConditionalOnMissingBean(name = "liquibase")
    public SpringLiquibase liquibase(ResourceLoader resourceLoader) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLog);
        liquibase.setResourceLoader(resourceLoader);
        // This is because we are running the process manually. Don't let SpringLiquibase do it.
        liquibase.setShouldRun(false);
        return liquibase;
    }

    /**
     * Creates a LiquibaseService bean if one doesn't already exist.
     * This bean will only be created if LiquibaseDataSource is available in the context.
     *
     * @param liquibaseProperties the Liquibase configuration properties
     * @param springLiquibase the SpringLiquibase bean
     * @param liquibaseDataSource the tenant data source provider
     * @return LiquibaseService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public LiquibaseService liquibaseService(
            LiquibaseProperties liquibaseProperties,
            SpringLiquibase springLiquibase,
            LiquibaseDataSource liquibaseDataSource) {
        return new LiquibaseService(liquibaseProperties, springLiquibase, liquibaseDataSource);
    }
}
