# Spring Tenancy Liquibase

A powerful Spring Boot module for Liquibase-based database schema management in multi-tenant applications. This library provides automatic configuration, flexible initialization strategies, and seamless integration with Spring Boot's auto-configuration system.

> **✨ Spring Boot 2.7.x & 3.x Compatible** | **☕ Java 11+ (Spring Boot 2) / Java 17+ (Spring Boot 3)**

## Features

- 🏢 **Multi-Tenant Support** - Manage database schemas for multiple tenants seamlessly
- 🚀 **Spring Boot Auto-Configuration** - Zero-configuration setup, works out of the box
- 🔄 **Flexible Initialization** - Support for both automatic and on-demand schema initialization
- 💾 **Connection Pooling** - Built-in HikariCP configuration for each tenant
- 🗄️ **Multi-Database Support** - MySQL, PostgreSQL, SQL Server, and Oracle
- ⚙️ **Highly Configurable** - Full control over connection pool and Liquibase settings
- 🎯 **Interface-Based Design** - Extensible architecture for custom tenant management
- 📝 **SLF4J Logging** - Comprehensive logging for troubleshooting and monitoring
- 🛡️ **Safe Defaults** - Production-ready configuration out of the box

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:spring-tenancy-liquibase:1.0.0'
    implementation 'org.liquibase:liquibase-core:4.20.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>spring-tenancy-liquibase</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
    <version>4.20.0</version>
</dependency>
```

> **✨ Auto-Configuration**: This module uses Spring Boot auto-configuration. Simply add the dependency and the `LiquibaseService` bean will be automatically available for injection - no manual configuration or component scanning required!

## Configuration

### Application Properties

Configure the module in your `application.yml` or `application.properties`:

#### application.yml

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    
  datasource:
    liquibase:
      # Initialization Settings
      initialize-on-startup: false  # Set to true to run migrations on application startup
      
      # HikariCP Connection Pool Settings
      maximum-pool-size: 2
      minimum-idle: 1
      connection-timeout: 30000      # 30 seconds
      idle-timeout: 300000           # 5 minutes
      max-lifetime: 900000           # 15 minutes
      leak-detection-threshold: 60000 # 1 minute
      validation-timeout: 5000       # 5 seconds
```

#### application.properties

```properties
# Liquibase Changelog
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Initialization Settings
spring.datasource.liquibase.initialize-on-startup=false

# HikariCP Connection Pool Settings
spring.datasource.liquibase.maximum-pool-size=2
spring.datasource.liquibase.minimum-idle=1
spring.datasource.liquibase.connection-timeout=30000
spring.datasource.liquibase.idle-timeout=300000
spring.datasource.liquibase.max-lifetime=900000
spring.datasource.liquibase.leak-detection-threshold=60000
spring.datasource.liquibase.validation-timeout=5000
```

### Property Descriptions

| Property | Default | Description |
|----------|---------|-------------|
| `initialize-on-startup` | `false` | When `true`, automatically runs migrations for all tenants on startup |
| `maximum-pool-size` | `2` | Maximum number of connections in the pool per tenant |
| `minimum-idle` | `1` | Minimum number of idle connections maintained in the pool |
| `connection-timeout` | `30000` | Maximum time (ms) to wait for a connection from the pool |
| `idle-timeout` | `300000` | Maximum time (ms) a connection can sit idle in the pool |
| `max-lifetime` | `900000` | Maximum lifetime (ms) of a connection in the pool |
| `leak-detection-threshold` | `60000` | Time (ms) before a connection leak is logged (0 = disabled) |
| `validation-timeout` | `5000` | Maximum time (ms) to wait for connection validation |

## Implementing Tenant Data Source Provider

Create a component that implements `LiquibaseDataSource` to provide tenant information:

```java
import com.javaquery.spring.liquibase.LiquibaseDataSource;
import com.javaquery.spring.liquibase.TenantDataSource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyLiquibaseDataSource implements LiquibaseDataSource {
    
    private final TenantRepository tenantRepository;
    
    public MyLiquibaseDataSource(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    
    @Override
    public List<TenantDataSource> getAllTenants() {
        // Fetch tenant configurations from database, config file, etc.
        return tenantRepository.findAll().stream()
            .map(this::convertToTenantDataSource)
            .collect(Collectors.toList());
    }
    
    private TenantDataSource convertToTenantDataSource(Tenant tenant) {
        return new TenantDataSource() {
            @Override
            public String getTenantName() {
                return tenant.getName();
            }
            
            @Override
            public String getHost() {
                return tenant.getDbHost();
            }
            
            @Override
            public Integer getPort() {
                return tenant.getDbPort();
            }
            
            @Override
            public String getSchemaName() {
                return tenant.getDbSchema();
            }
            
            @Override
            public String getUsername() {
                return tenant.getDbUsername();
            }
            
            @Override
            public String getPassword() {
                return tenant.getDbPassword();
            }
            
            @Override
            public String getDriver() {
                return tenant.getDbDriver(); // e.g., "com.mysql.cj.jdbc.Driver"
            }
            
            @Override
            public String getDialect() {
                return tenant.getDbDialect(); // "MySQL", "PostgreSQL", "SQLServer", or "Oracle"
            }
        };
    }
}
```

## Usage

### Automatic Initialization (On Application Startup)

Set `initialize-on-startup: true` to automatically run Liquibase migrations for all tenants when the application starts:

```yaml
spring:
  datasource:
    liquibase:
      initialize-on-startup: true
```

This will execute migrations for all tenants returned by `LiquibaseDataSource.getAllTenants()` during application startup.

### Manual Initialization (On-Demand)

Inject `LiquibaseService` and call methods to manually initialize tenant schemas:

```java
import com.javaquery.spring.liquibase.LiquibaseService;
import com.javaquery.spring.liquibase.TenantDataSource;
import liquibase.exception.LiquibaseException;
import org.springframework.stereotype.Service;

@Service
public class TenantProvisioningService {
    
    private final LiquibaseService liquibaseService;
    
    public TenantProvisioningService(LiquibaseService liquibaseService) {
        this.liquibaseService = liquibaseService;
    }
    
    /**
     * Initialize schema for a single tenant
     */
    public void provisionNewTenant(TenantDataSource tenantDataSource) throws LiquibaseException {
        liquibaseService.initSchemaForTenant(tenantDataSource);
        // Schema is now ready for the tenant
    }
    
    /**
     * Initialize schemas for all tenants
     */
    public void provisionAllTenants() throws LiquibaseException {
        liquibaseService.initSchemaForAllTenants();
        // All tenant schemas are now initialized
    }
}
```

### Dynamic Tenant Provisioning Example

```java
@RestController
@RequestMapping("/api/tenants")
public class TenantManagementController {
    
    private final TenantRepository tenantRepository;
    private final LiquibaseService liquibaseService;
    
    public TenantManagementController(
            TenantRepository tenantRepository,
            LiquibaseService liquibaseService) {
        this.tenantRepository = tenantRepository;
        this.liquibaseService = liquibaseService;
    }
    
    @PostMapping
    public ResponseEntity<String> createTenant(@RequestBody TenantRequest request) {
        try {
            // 1. Save tenant configuration to database
            Tenant tenant = tenantRepository.save(request.toTenant());
            
            // 2. Initialize Liquibase schema for the new tenant
            TenantDataSource tenantDataSource = tenant.toTenantDataSource();
            liquibaseService.initSchemaForTenant(tenantDataSource);
            
            return ResponseEntity.ok("Tenant created and provisioned successfully");
        } catch (LiquibaseException e) {
            return ResponseEntity.status(500).body("Failed to provision tenant: " + e.getMessage());
        }
    }
}
```

## Liquibase Changelog Structure

Create your Liquibase changelog files in `src/main/resources/db/changelog/`:

### db.changelog-master.yaml

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-initial-schema.yaml
  - include:
      file: db/changelog/changes/002-add-users-table.yaml
```

### changes/001-initial-schema.yaml

```yaml
databaseChangeLog:
  - changeSet:
      id: 001
      author: developer
      changes:
        - createTable:
            tableName: company
            columns:
              - column:
                  name: id
                  type: bigint
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: timestamp
                  defaultValueComputed: CURRENT_TIMESTAMP
```

## Supported Databases

The module automatically generates connection URLs for the following databases:

| Database | Dialect Value | JDBC Driver |
|----------|---------------|-------------|
| MySQL | `MySQL` | `com.mysql.cj.jdbc.Driver` |
| PostgreSQL | `PostgreSQL` | `org.postgresql.Driver` |
| SQL Server | `SQLServer` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| Oracle | `Oracle` | `oracle.jdbc.OracleDriver` |

## Auto-Configuration Details

This module uses Spring Boot's auto-configuration mechanism:

- **`LiquibaseAutoConfiguration`**: Automatically configures the Liquibase service when the module is on the classpath
- **Conditional Beans**: Beans are only created when Liquibase and DataSource classes are available
- **Property Binding**: Automatically binds `spring.datasource.liquibase.*` properties to `LiquibaseProperties`
- **Customizable**: All beans can be overridden by defining your own beans with the same name

The auto-configuration is registered in:
- `META-INF/spring.factories` (Spring Boot 2.x compatibility)
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Spring Boot 2.7+/3.x)

## Requirements

- **Spring Boot**: 2.7.x or 3.x
- **Java**: 11+ (Spring Boot 2.x) or 17+ (Spring Boot 3.x)
- **Liquibase Core**: 4.x or higher
- **HikariCP**: Included with Spring Boot
- **JDBC Driver**: Appropriate driver for your database

## Best Practices

1. **Connection Pool Sizing**: Keep `maximum-pool-size` small (2-5) for Liquibase operations to avoid resource exhaustion
2. **Leak Detection**: Enable `leak-detection-threshold` in development to catch connection leaks early
3. **Startup Initialization**: Use `initialize-on-startup: false` and initialize on-demand for better control
4. **Error Handling**: Always wrap `LiquibaseService` calls in try-catch blocks to handle `LiquibaseException`
5. **Tenant Validation**: Validate tenant configurations before passing to `initSchemaForTenant()`

## Troubleshooting

### Common Issues

**Issue**: Migrations not running on startup
- **Solution**: Ensure `initialize-on-startup: true` is set and `LiquibaseDataSource` bean is properly registered

**Issue**: Connection timeouts
- **Solution**: Increase `connection-timeout` or check database connectivity

**Issue**: Bean not found
- **Solution**: Ensure `org.liquibase:liquibase-core` is in your dependencies

## License

See the main project LICENSE file.


## Version

1.0.0
