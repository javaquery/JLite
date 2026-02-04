# Changelog

All notable changes to the module:spring-tenancy-liquibase module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-04

### Added

#### Core Features
- **Multi-Tenant Liquibase Support** - Automated database schema management for multi-tenant applications
  - Unified interface for managing multiple tenant databases
  - Automatic schema initialization and migration per tenant
  - Support for on-demand tenant provisioning
- **Spring Boot Auto-Configuration** - Zero-configuration setup
  - Works out-of-the-box when added to classpath
  - Automatic bean registration and dependency injection
  - Conditional loading based on required dependencies
- **Flexible Initialization** - Multiple initialization strategies
  - Automatic initialization on application startup
  - Manual initialization for on-demand tenant provisioning
  - Configurable via Spring Boot properties

#### Connection Management
- **HikariCP Integration** - Optimized connection pooling for each tenant
  - Configurable pool size, timeout, and lifecycle settings
  - Automatic connection cleanup and leak detection
  - Per-tenant connection pool management
- **Multi-Database Support** - Connection URL generation for multiple databases
  - MySQL
  - PostgreSQL
  - SQL Server
  - Oracle

#### Configuration & Integration
- **LiquibaseProperties** - Spring Boot property binding
  - `spring.datasource.liquibase.initialize-on-startup` - Control automatic initialization
  - `spring.datasource.liquibase.maximum-pool-size` - Configure connection pool size
  - `spring.datasource.liquibase.minimum-idle` - Configure minimum idle connections
  - `spring.datasource.liquibase.connection-timeout` - Configure connection timeout
  - `spring.datasource.liquibase.idle-timeout` - Configure idle timeout
  - `spring.datasource.liquibase.max-lifetime` - Configure maximum connection lifetime
  - `spring.datasource.liquibase.leak-detection-threshold` - Configure leak detection
  - `spring.datasource.liquibase.validation-timeout` - Configure validation timeout
- **LiquibaseDataSource Interface** - Extensible tenant data source provider
  - Implement to provide tenant connection information
  - Flexible integration with any tenant storage mechanism
  - Support for dynamic tenant discovery
- **TenantDataSource Interface** - Individual tenant configuration
  - Host, port, schema name
  - Database credentials
  - Driver and dialect information

#### Core Classes
- **LiquibaseService** - Main service for tenant schema management
  - `initSchemaForTenant()` - Initialize or update a specific tenant's schema
  - `initSchemaForAllTenants()` - Batch initialization for all tenants
  - Automatic Liquibase changelog execution
  - HikariCP connection pooling per tenant
- **LiquibaseAutoConfiguration** - Spring Boot auto-configuration
  - Automatic bean creation and wiring
  - Conditional on Liquibase presence
  - Proper ordering with Spring's LiquibaseAutoConfiguration
  - Disables default Spring Boot Liquibase auto-run
- **Dual Registration System** - Spring Boot 2.x and 3.x compatibility
  - `META-INF/spring.factories` for Spring Boot 2.x backward compatibility
  - `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` for Spring Boot 2.7+/3.x

#### Interfaces & Extensibility
- **LiquibaseDataSource** - Tenant data source provider interface
  - `getAllTenants()` - Retrieve all tenant configurations
  - Implement to integrate with your tenant storage mechanism
- **TenantDataSource** - Individual tenant configuration interface
  - `getTenantName()` - Unique tenant identifier
  - `getHost()`, `getPort()` - Database connection details
  - `getSchemaName()` - Target schema name
  - `getUsername()`, `getPassword()` - Database credentials
  - `getDriver()` - JDBC driver class name
  - `getDialect()` - Database dialect (MySQL, PostgreSQL, SQLServer, Oracle)

### Technical Details

#### Auto-Configuration
- Uses `@AutoConfiguration` with proper ordering (`AutoConfigureAfter(LiquibaseAutoConfiguration.class)`)
- Implements `@ConditionalOnClass` for conditional loading (Liquibase and DataSource required)
- Provides `@ConditionalOnMissingBean` for customization
- Constructor injection for better testability
- Enables configuration properties with `@EnableConfigurationProperties(LiquibaseProperties.class)`

#### Architecture
- Follows Spring Boot auto-configuration best practices
- Clean separation of concerns with interfaces
- Extensible design for custom tenant management
- Compatible with Spring Boot 2.x and 3.x
- No manual configuration or component scanning required

#### Connection Management
- Uses HikariCP for efficient connection pooling
- Separate connection pool per tenant
- Automatic URL generation based on database dialect
- Configurable pool parameters via Spring properties

#### Dependencies
- Liquibase Core (compile-only, must be provided by consuming application)
- Spring Boot Starter (provided by spring-conventions plugin)
- HikariCP (included with Spring Boot)
- SLF4J for logging

### Compatibility
- **Spring Boot 2.7.x** (Java 11+) - ✅ Supported
- **Spring Boot 3.0.x - 3.5.x** (Java 17+) - ✅ Supported
- **Liquibase Core** - Compatible with standard Liquibase versions
