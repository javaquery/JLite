# core:spring

A collection of utility classes and abstractions for Spring Boot projects, providing common patterns for service layers, repository specifications, and JSON deserialization.

## Features

- 🎯 **Abstract Service Layer** - Base implementation for CRUD operations with Spring Data JPA
- 🔍 **Specification Builders** - Fluent API for building JPA Specifications
- 📄 **Pagination Support** - Simple PageData wrapper for paginated results
- 📅 **JSON Deserializers** - Custom deserializers for LocalDateTime and String trimming
- ⚡ **Event Publishing** - Built-in support for Spring ApplicationEventPublisher
- 🛡️ **Exception Handling** - Flexible error handling with supplier-based exceptions

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:spring:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>spring</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Quick Start

### Abstract Service

The `AbstractService` provides common CRUD operations out of the box, reducing boilerplate code in your service layer.

#### Basic Usage

```java
import com.javaquery.spring.service.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends AbstractService<Customer, Long> implements CustomerService {

    public CustomerServiceImpl(CustomerRepository repository, 
                              ApplicationEventPublisher applicationEventPublisher) {
        super(repository, applicationEventPublisher);
    }
    
    // All CRUD operations are inherited, add custom methods as needed
}
```

#### Available Operations

```java
// Save single entity
Customer customer = customerService.save(customer);

// Save multiple entities
List<Customer> customers = customerService.saveAll(customerList);

// Find by ID
Customer customer = customerService.findById(1L, null);

// Find by ID with exception handling
Customer customer = customerService.findById(1L, 
    () -> new NotFoundException("Customer not found"));

// Delete by ID
Customer deleted = customerService.deleteById(1L, null);

// Delete entity
customerService.delete(customer);

// Check existence
boolean exists = customerService.existsById(1L, null);

// Find all by IDs
List<Customer> customers = customerService.findAllById(List.of(1L, 2L, 3L));

// Find all with pagination
PageData<Customer> page = customerService.findAll(Pageable.of(0, 10));

// Count records
long count = customerService.count();
```

### Specification Builders

The `AbstractSpecification` interface provides convenient methods for building JPA Specifications.

#### Repository Setup

```java
import com.javaquery.spring.repository.AbstractSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>,
                                            JpaSpecificationExecutor<Customer>,
                                            AbstractSpecification<Customer> {
}
```

#### Using Specifications

```java
import org.springframework.data.jpa.domain.Specification;

// Equal comparison
Specification<Customer> spec = customerRepository.equal("status", "ACTIVE");

// Not equal
spec = customerRepository.notEqual("status", "DELETED");

// In collection
spec = customerRepository.in("id", List.of(1L, 2L, 3L));

// Not in collection
spec = customerRepository.notIn("status", List.of("DELETED", "SUSPENDED"));

// Like pattern
spec = customerRepository.like("email", "%@example.com");

// Starts with
spec = customerRepository.startsWith("firstName", "John");

// Ends with
spec = customerRepository.endsWith("email", "@gmail.com");

// Contains
spec = customerRepository.contains("name", "smith");
```

#### Combining Specifications

```java
// Combine multiple specifications
Specification<Customer> spec = Specification
    .where(customerRepository.equal("status", "ACTIVE"))
    .and(customerRepository.contains("email", "@gmail.com"))
    .and(customerRepository.startsWith("firstName", "J"));

// Use with service
PageData<Customer> results = customerService.findAll(spec, Pageable.of(0, 20));
List<Customer> allResults = customerService.findAll(spec);
```

### PageData

A simple wrapper for paginated results that includes metadata about the page.

```java
import com.javaquery.spring.data.PageData;

PageData<Customer> page = customerService.findAll(Pageable.of(0, 20));

long totalElements = page.getTotalElements();  // Total records
int totalPages = page.getTotalPages();          // Total pages
int currentPage = page.getCurrentPage();        // Current page number
int pageSize = page.getPageSize();              // Page size
List<Customer> data = page.getData();           // Actual data
```

### JSON Deserializers

The module includes custom Jackson deserializers that are automatically registered with Spring Boot.

#### LocalDateTimeJsonDeserializer

Flexibly deserializes dates and datetimes from multiple formats:

- ISO format with 'T': `2025-07-25T10:30:45`
- Space-separated: `2025-07-25 10:30:45`
- Date only: `2025-07-25` (converted to start of day)

```java
public class Event {
    private LocalDateTime startDate;  // Automatically uses custom deserializer
    private LocalDateTime endDate;
}

// JSON input - all formats supported:
// {"startDate": "2025-07-25T10:30:45", "endDate": "2025-07-25"}
```

#### StringJsonDeserializer

Automatically trims leading and trailing whitespace from all string values during deserialization.

```java
public class Customer {
    private String name;  // Automatically trimmed
    private String email;
}

// JSON input: {"name": "  John Doe  ", "email": " john@example.com "}
// Result: name = "John Doe", email = "john@example.com"
```

## Advanced Usage

### Custom Service Methods

```java
@Service
public class CustomerServiceImpl extends AbstractService<Customer, Long> 
                                 implements CustomerService {

    public CustomerServiceImpl(CustomerRepository repository, 
                              ApplicationEventPublisher applicationEventPublisher) {
        super(repository, applicationEventPublisher);
    }
    
    // Add custom business logic
    public List<Customer> findActiveCustomers() {
        Specification<Customer> spec = 
            ((CustomerRepository) repository).equal("status", "ACTIVE");
        return findAll(spec);
    }
    
    public Customer activateCustomer(Long id) {
        Customer customer = findById(id, 
            () -> new NotFoundException("Customer not found"));
        customer.setStatus("ACTIVE");
        
        // Use the event publisher for domain events
        applicationEventPublisher.publishEvent(
            new CustomerActivatedEvent(customer));
        
        return save(customer);
    }
}
```

### Exception Handling Patterns

```java
// Pattern 1: Return null if not found
Customer customer = customerService.findById(id, null);
if (customer == null) {
    // Handle not found case
}

// Pattern 2: Throw custom exception if not found
Customer customer = customerService.findById(id, 
    () -> new CustomerNotFoundException("Customer with id " + id + " not found"));

// Pattern 3: Check existence and throw if not exists
boolean exists = customerService.existsById(id, 
    () -> new CustomerNotFoundException("Customer with id " + id + " not found"));
```

### Complex Specifications

```java
public interface CustomerRepository extends JpaRepository<Customer, Long>,
                                            JpaSpecificationExecutor<Customer>,
                                            AbstractSpecification<Customer> {
    
    default Specification<Customer> isActive() {
        return equal("status", "ACTIVE");
    }
    
    default Specification<Customer> hasEmailDomain(String domain) {
        return endsWith("email", "@" + domain);
    }
    
    default Specification<Customer> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThan(root.get("createdAt"), date);
    }
}

// Usage
Specification<Customer> spec = Specification
    .where(customerRepository.isActive())
    .and(customerRepository.hasEmailDomain("gmail.com"))
    .and(customerRepository.createdAfter(LocalDateTime.now().minusDays(30)));

List<Customer> recentGmailCustomers = customerService.findAll(spec);
```

## API Reference

### AbstractService<T, ID>

Base class for service implementations providing CRUD operations.

**Methods:**

- `T save(T entity)` - Saves an entity
- `<S extends T> List<S> saveAll(Iterable<S> entities)` - Saves multiple entities
- `T findById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound)` - Finds entity by ID
- `T deleteById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound)` - Deletes entity by ID
- `void delete(T entity)` - Deletes an entity
- `boolean existsById(ID id, Supplier<? extends RuntimeException> throwExceptionIfNotFound)` - Checks existence
- `List<T> findAllById(Iterable<ID> ids)` - Finds multiple entities by IDs
- `PageData<T> findAll(Specification<T> specification, Pageable pageable)` - Finds with specification and pagination
- `List<T> findAll(Specification<T> specification)` - Finds with specification
- `PageData<T> findAll(Pageable pageable)` - Finds with pagination
- `long count()` - Counts total entities

### AbstractSpecification<T>

Interface providing convenient specification builders.

**Methods:**

- `Specification<T> equal(String field, Object value)` - Field equals value
- `Specification<T> notEqual(String field, Object value)` - Field not equals value
- `Specification<T> in(String field, Iterable<?> values)` - Field in collection
- `Specification<T> notIn(String field, Iterable<?> values)` - Field not in collection
- `Specification<T> like(String field, String pattern)` - Field matches pattern
- `Specification<T> startsWith(String field, String prefix)` - Field starts with prefix
- `Specification<T> endsWith(String field, String suffix)` - Field ends with suffix
- `Specification<T> contains(String field, String infix)` - Field contains substring

### PageData<T>

Immutable wrapper for paginated results.

**Properties:**

- `long totalElements` - Total number of elements
- `int totalPages` - Total number of pages
- `int currentPage` - Current page number (0-based)
- `int pageSize` - Number of elements per page
- `List<T> data` - The actual data for this page

## Requirements

- Java 11 or higher
- Spring Boot 2.x, 3.x, etc...
- Spring Data JPA
- Lombok (for PageData)

## License

This project is part of the JLite library suite.

## Contributing

Contributions are welcome! Please ensure all tests pass before submitting pull requests.

## Author

**javaquery**

## Version

Current version: **1.0.1**

