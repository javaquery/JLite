package com.javaquery.spring.liquibase;

import java.util.List;

/**
 * Provides data source information for Liquibase operations.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface LiquibaseDataSource {
    /**
     * Retrieves a list of all tenant data sources.
     * @return List of TenantDataSource objects representing each tenant's data source information.
     */
    List<TenantDataSource> getAllTenants();
}
