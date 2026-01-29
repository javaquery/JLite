# Changelog

All notable changes to the core:spring module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-01-29

### Added

#### Spring Boot Auto-Configuration
- **SpringUtilAutoConfiguration** - Automatic configuration for Spring utilities
  - Automatic bean registration via auto-configuration
  - Zero configuration setup - works out of the box
  - No manual component scanning required
  - Conditional activation based on Jackson presence
  - Respects custom bean implementations
- **ObjectMapperService Auto-Configuration**
  - Automatically creates `ObjectMapperService` bean
  - Automatically creates `snakeCaseObjectMapper` bean
  - Only activates when `ObjectMapper` class is present
  - `@ConditionalOnMissingBean` to respect custom implementations

#### JSON Conversion Service
- **ObjectMapperService** - Service for Jackson ObjectMapper operations
  - `toJson(Object)` - Convert object to JSON string (camelCase)
  - `toSnakeCaseJson(Object)` - Convert object to JSON with snake_case properties
  - `convertValue(Object, Class<T>)` - Convert between object types
  - `readValue(String, Class<T>)` - Deserialize JSON string to object
  - `readSnakeCaseJson(String, Class<T>)` - Deserialize snake_case JSON to object
  - Support for both camelCase and snake_case naming strategies
  - Graceful error handling with null returns on conversion failures

#### META-INF Registration
- `spring.factories` file for Spring Boot 2.x auto-configuration
- Automatic discovery by Spring Boot
- No manual registration required

### Changed
- **ObjectMapperService** - Removed `@Service` annotation (now created via auto-configuration)
- **BeanConfiguration** - Removed (functionality moved to SpringUtilAutoConfiguration)
- Improved Javadoc comments for ObjectMapperService
- Enhanced documentation with clearer method descriptions

### Removed
- `BeanConfiguration.java` - Replaced with auto-configuration
- Manual `@Service` annotation from `ObjectMapperService`
- Requirement for manual component scanning

### Technical Details

#### Dependencies Added
- `spring-boot-autoconfigure` (implementation)
- `spring-boot-autoconfigure-processor` (annotationProcessor)

#### Build Configuration
- Added auto-configuration dependencies
- Configuration metadata generation support

---

## [1.0.0] - Initial Release

### Added

#### Core Features
- **AbstractService<T, ID>** - Abstract base class for service layer
  - Generic CRUD operations
  - JPA Repository integration
  - JPA Specification support
  - Event publishing capabilities
  - Type-safe implementations
- **IAbstractService<T, ID>** - Service interface with default implementations
  - `save(T)` - Save entity
  - `saveAll(Iterable<S>)` - Batch save entities
  - `findById(ID, Supplier)` - Find with optional exception throwing
  - `deleteById(ID, Supplier)` - Delete with optional exception throwing
  - `delete(T)` - Delete entity
  - `existsById(ID, Supplier)` - Check existence with optional exception
  - `findAllById(Iterable<ID>)` - Find multiple by IDs
  - `findAll(Specification, Pageable)` - Find with specification and pagination
  - `findAll(Specification)` - Find all matching specification
  - `findAll(Pageable)` - Find all with pagination
  - `count()` - Count all entities

#### Data Utilities
- **PageData<T>** - Pagination wrapper class
  - `totalElements` - Total number of elements across all pages
  - `totalPages` - Total number of pages
  - `currentPage` - Current page number
  - `pageSize` - Number of elements per page
  - `data` - List of elements in current page
  - Immutable design with Lombok `@Getter`
  - Type-safe generic implementation

#### Repository Utilities
- **AbstractSpecification<T>** - JPA Specification builder interface
  - `equal(field, value)` - Equality check
  - `notEqual(field, value)` - Inequality check
  - `in(field, ids)` - IN clause
  - `notIn(field, ids)` - NOT IN clause
  - `like(field, pattern)` - Pattern matching with LIKE
  - `startsWith(field, prefix)` - Starts with pattern
  - `endsWith(field, suffix)` - Ends with pattern
  - `isNull(field)` - NULL check
  - `isNotNull(field)` - NOT NULL check
  - `greaterThan(field, value)` - Greater than comparison
  - `lessThan(field, value)` - Less than comparison
  - `greaterThanOrEqual(field, value)` - Greater than or equal comparison
  - `lessThanOrEqual(field, value)` - Less than or equal comparison
  - Default method implementations for easy specification building
  - Composable specifications with `and()` and `or()`

#### JSON Deserializers
- **LocalDateTimeJsonDeserializer** - Custom deserializer for LocalDateTime
  - Support for multiple date-time formats
  - ISO 8601 format support
  - Epoch timestamp support (seconds and milliseconds)
  - String format with configurable patterns
  - Graceful error handling
- **StringJsonDeserializer** - Custom deserializer for String
  - Handles various JSON value types
  - Converts numbers to strings
  - Converts booleans to strings
  - Trims whitespace
  - Null-safe implementation

### Technical Details

#### Architecture
- **Service Layer Pattern** - Clean separation of concerns with AbstractService
- **Repository Pattern** - JPA integration with specification support
- **Generic Programming** - Type-safe implementations with generics
- **Event-Driven** - ApplicationEventPublisher integration

#### Dependencies
- Spring Boot Starter (provided by conventions)
- Spring Data JPA (provided by conventions)
- JLite Util module (api)
- Jackson Databind (test scope)
- H2 Database (test scope)

#### Design Patterns
- **Abstract Factory** - AbstractService provides factory for service implementations
- **Template Method** - Default implementations in IAbstractService
- **Builder Pattern** - Specification builders in AbstractSpecification
- **Strategy Pattern** - Custom deserializers for different types

### Integration Features

#### Spring Data JPA Integration
- Works seamlessly with JPA repositories
- Automatic specification executor detection
- Support for all Spring Data JPA query methods
- Transaction management support

#### Spring Boot Integration
- Compatible with Spring Boot auto-configuration
- Works with Spring Boot Starter Data JPA
- Integration with Spring Boot testing framework
- Event publishing through ApplicationContext

#### Event Publishing
- Automatic event publishing through ApplicationEventPublisher
- Integration with Spring's event system
- Decoupled architecture through events
- Support for custom event listeners

### Usage Patterns

#### Service Implementation Example
```java
@Service
public class CustomerService extends AbstractService<Customer, Long> {
    public CustomerService(CustomerRepository repository, 
                          ApplicationEventPublisher eventPublisher) {
        super(repository, eventPublisher);
    }
    
    // Additional custom methods
}
```

#### Specification Usage Example
```java
public interface CustomerRepository extends JpaRepository<Customer, Long>, 
                                           JpaSpecificationExecutor<Customer> {}

public class CustomerSpecification implements AbstractSpecification<Customer> {
    public Specification<Customer> activeCustomers() {
        return equal("status", "ACTIVE");
    }
    
    public Specification<Customer> byName(String name) {
        return like("name", "%" + name + "%");
    }
}
```

#### Pagination Example
```java
Pageable pageable = PageRequest.of(0, 20);
PageData<Customer> page = customerService.findAll(pageable);
```

### Testing Support
- H2 in-memory database for testing
- Spring Boot Test integration
- JPA test configuration
- Mock support with test implementations

### Performance Considerations
- Lazy loading support in AbstractService
- Efficient pagination with Spring Data
- Specification-based queries for dynamic filtering
- Batch operations support

### Known Limitations
- AbstractService requires JpaRepository
- Specification builder requires JpaSpecificationExecutor
- Event publishing requires ApplicationEventPublisher bean
- Jackson deserializers require Jackson on classpath

### Breaking Changes
- None (initial release)

### Deprecations
- None (initial release)

### Security
- No known security vulnerabilities
- Uses Spring Data JPA's built-in SQL injection prevention
- Safe parameter binding in specifications

### Contributors
- Vicky Thakor (@javaquery)

### Links
- [GitHub Repository](https://github.com/javaquery/JLite)
- [Documentation](README.md)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---

## Version History

### Release Schedule
- **1.0.0** - Initial release with core Spring utilities
- **1.0.1** - January 29, 2026 - Added auto-configuration support

### Compatibility Matrix

| Module Version | Spring Boot | Java | Spring Data JPA | Status |
|----------------|-------------|------|-----------------|--------|
| 1.0.1 | 2.7.x | 11+ | 2.7.x | ✅ Stable |
| 1.0.0 | 2.7.x | 11+ | 2.7.x | ✅ Stable |

### Future Roadmap

#### Version 1.1.0 (Planned)
- **Enhanced Specification Builders**
  - More complex query operators
  - Join support
  - Subquery support
  - Aggregation functions
- **Audit Support**
  - Created/Modified timestamp tracking
  - User tracking for changes
  - Audit event publishing
- **Soft Delete Support**
  - Logical deletion markers
  - Automatic filtering of deleted records
  - Restore functionality
- **Caching Integration**
  - Cache abstraction support
  - Automatic cache invalidation
  - Cache configuration utilities

#### Version 1.2.0 (Planned)
- **Spring Boot 3.x Support**
  - Update auto-configuration registration
  - Jakarta EE namespace migration
  - Native compilation support
- **Advanced Query Features**
  - Dynamic projection support
  - Query result transformers
  - Custom query methods
- **Validation Integration**
  - Bean validation support
  - Custom validators
  - Validation groups
- **Transaction Management**
  - Declarative transaction utilities
  - Transaction template wrappers
  - Rollback strategies

#### Version 2.0.0 (Future)
- **Reactive Support**
  - Reactive repository support
  - WebFlux integration
  - R2DBC support
- **Multi-Tenancy Support**
  - Tenant isolation
  - Tenant-aware queries
  - Schema-based multi-tenancy
- **Advanced Event System**
  - Domain events
  - Event sourcing support
  - CQRS patterns
- **Metrics and Monitoring**
  - Performance metrics
  - Query statistics
  - Health indicators

### Upgrade Guides

#### From 1.0.0 to 1.0.1

**Auto-Configuration Changes:**

1. **Remove Manual Component Scanning** (Optional but Recommended)
   ```java
   // Before
   @SpringBootApplication
   @ComponentScan(basePackages = {"com.myapp", "com.javaquery.spring"})
   public class MyApplication {
   }
   
   // After
   @SpringBootApplication
   public class MyApplication {
       // Auto-configuration handles ObjectMapperService
   }
   ```

2. **Remove Manual Bean Configuration** (Optional but Recommended)
   ```java
   // Before - Manual configuration
   @Configuration
   public class MyConfig {
       @Bean
       @Qualifier("snakeCaseObjectMapper")
       public ObjectMapper snakeCaseObjectMapper() {
           ObjectMapper mapper = new ObjectMapper();
           mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
           return mapper;
       }
       
       @Bean
       public ObjectMapperService objectMapperService(
               ObjectMapper objectMapper,
               @Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
           return new ObjectMapperService(objectMapper, snakeCaseObjectMapper);
       }
   }
   
   // After - Auto-configured (no manual configuration needed)
   // Just inject ObjectMapperService anywhere
   ```

3. **Update Dependencies**
   ```gradle
   implementation 'com.javaquery:spring:1.0.1'
   ```

4. **Verify Injection**
   ```java
   @Service
   public class MyService {
       private final ObjectMapperService objectMapperService;
       
       public MyService(ObjectMapperService objectMapperService) {
           this.objectMapperService = objectMapperService;  // Auto-injected
       }
   }
   ```

**What Stays the Same:**
- AbstractService usage remains unchanged
- IAbstractService interface unchanged
- PageData usage unchanged
- AbstractSpecification usage unchanged
- All existing functionality preserved
- No breaking changes to existing code

**Benefits of Upgrading:**
- ✅ Zero configuration for ObjectMapperService
- ✅ No component scanning required
- ✅ Automatic snake_case ObjectMapper creation
- ✅ Cleaner application code
- ✅ Follows Spring Boot best practices

### Support and Feedback
- **Issues**: [GitHub Issues](https://github.com/javaquery/JLite/issues)
- **Discussions**: [GitHub Discussions](https://github.com/javaquery/JLite/discussions)
- **Documentation**: [README.md](README.md)

---

*For detailed usage examples and API documentation, see [README.md](README.md)*
