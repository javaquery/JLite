package com.javaquery.spring.liquibase;

import java.util.List;

/**
 * Provides data source information for Liquibase operations.
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface LiquibaseDataSource {
    List<TenantDataSource> getAllTenants();
}
