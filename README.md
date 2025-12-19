# JLite

**JLite** is a comprehensive Java library suite providing utility functions, Spring Boot extensions, and modular components to accelerate application development. Built with modern Java practices, JLite offers a collection of battle-tested utilities and abstractions for common development tasks.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green.svg)](https://spring.io/projects/spring-boot)

## 📚 Table of Contents

- [Features](#-features)
- [Modules](#-modules)
  - [Core Modules](#core-modules)
  - [Extension Modules](#extension-modules)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Building from Source](#-building-from-source)
- [Requirements](#-requirements)
- [Documentation](#-documentation)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

- 🛠️ **Utility Functions** - String manipulation, date/time operations, validation, and more
- 🏗️ **Spring Boot Abstractions** - Service layer patterns, repository specifications, and pagination
- 📧 **Email Support** - Simple API for sending emails with attachments and HTML content
- 📊 **CSV Processing** - Read and write CSV files with nested objects and collections
- ☁️ **AWS Integration** - Simplified AWS credentials configuration
- 🔄 **JSON Utilities** - Custom deserializers and JSON processing helpers
- 📝 **Type-Safe Builders** - Fluent APIs across all modules
- ✅ **Well-Tested** - Comprehensive test coverage

## 📦 Modules

### Core Modules

Core modules provide fundamental utilities and abstractions that can be used in any Java application.

#### [core:util](core/util/)

General-purpose utility library with common functions for everyday development tasks.

**Key Features:**
- String manipulation (`Strings`, `Regex`)
- Date and time utilities (`Dates`, `LocalDates`, `DateRange`)
- Validation helpers (`Assert`, `Is`)
- Number operations (`Numbers`)
- JSON processing (`JSON`)
- Logging utilities (`LogBuilder`)
- Unique ID generation (`UniqueIdGenerator`)

**Usage:**
```gradle
implementation 'com.javaquery:util:1.0.0'
```

**Example:**
```java
// String utilities
String result = Strings.nullOrEmpty(input, "default");
boolean isEmpty = Strings.nullOrEmpty(str);

// Date utilities
Date date = Dates.parse("2025-12-19", DatePattern.YYYY_MM_DD);
String formatted = Dates.format(new Date(), DatePattern.YYYY_MM_DD);

// Validation
Assert.notNull(object, "Object must not be null");
boolean isValid = Is.email("test@example.com");
```

---

#### [core:spring](core/spring/)

Spring Boot utilities providing common patterns for service layers, repositories, and data handling.

**Key Features:**
- Abstract service implementation with CRUD operations
- JPA Specification builders for dynamic queries
- Pagination support with `PageData`
- Custom JSON deserializers for `LocalDateTime` and `String`
- Built-in event publishing support

**Usage:**
```gradle
implementation 'com.javaquery:spring:1.0.0'
```

**Example:**
```java
@Service
public class CustomerService extends AbstractService<Customer, Long> {
    public CustomerService(CustomerRepository repository, 
                          ApplicationEventPublisher eventPublisher) {
        super(repository, eventPublisher);
    }
}

// Use built-in methods
Customer customer = customerService.findById(1L, 
    () -> new NotFoundException("Not found"));
PageData<Customer> page = customerService.findAll(Pageable.of(0, 20));
```

---

#### [core:httpclient](core/httpclient/)

HTTP client utilities (under development).

---

#### [core:ftpclient](core/ftpclient/)

FTP client utilities (under development).

---

### Extension Modules

Extension modules provide specialized functionality for specific use cases.

#### [module:ext-opencsv](module/ext-opencsv/)

Enhanced CSV processing library built on OpenCSV with support for batch processing, nested objects, and collections.

**Key Features:**
- Fluent builder API for reading and writing CSV files
- Batch processing for memory-efficient handling of large files
- Nested object support using dot notation
- Collection (List/Set) handling
- Annotation-based field mapping with `@Exportable`
- Custom row transformation

**Usage:**
```gradle
implementation 'com.javaquery:ext-opencsv:1.0.0'
```

**Example:**
```java
// Writing CSV
CsvWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Email"))
    .keys(List.of("firstName", "lastName", "email"))
    .data(customers)
    .toFile(new File("customers.csv"))
    .write();

// Reading CSV
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, values, prevRow) -> 
        new Customer(values[0], values[1], values[2]))
    .batchProcessor(batch -> repository.saveAll(batch))
    .batchSize(1000)
    .read();
```

[📖 Full Documentation](module/ext-opencsv/README.md)

---

#### [module:spring-email](module/spring-email/)

Lightweight Spring Boot email module with a clean API for sending emails.

**Key Features:**
- Builder pattern for email composition
- HTML and plain text support
- Multiple attachments
- TO, CC, BCC recipients
- Reply-to configuration
- Enable/disable via configuration

**Usage:**
```gradle
implementation 'com.javaquery:spring-email:1.0.0'
implementation 'org.springframework.boot:spring-boot-starter-mail'
```

**Example:**
```java
@Autowired
private EmailService emailService;

emailService.builder()
    .to("user@example.com")
    .subject("Welcome!")
    .htmlBody("<h1>Welcome to our service</h1>")
    .attachment(new File("document.pdf"))
    .send();
```

[📖 Full Documentation](module/spring-email/README.md)

---

#### [module:spring-aws](module/spring-aws/)

AWS integration utilities for Spring Boot applications.

**Key Features:**
- Automatic AWS credentials provider configuration
- Support for static credentials or default credentials chain
- Spring Boot auto-configuration

**Usage:**
```gradle
implementation 'com.javaquery:spring-aws:1.0.0'
```

**Configuration:**
```yaml
aws:
  accessKeyId: ${AWS_ACCESS_KEY_ID}
  secretAccessKey: ${AWS_SECRET_ACCESS_KEY}
  providerName: MyProvider
  accountId: 123456789012
```

---

## 🚀 Installation

### Using Gradle

Add the JLite modules you need to your `build.gradle`:

```gradle
dependencies {
    // Core utilities
    implementation 'com.javaquery:util:1.0.0'
    
    // Spring utilities
    implementation 'com.javaquery:spring:1.0.0'
    
    // CSV processing
    implementation 'com.javaquery:ext-opencsv:1.0.0'
    
    // Email support
    implementation 'com.javaquery:spring-email:1.0.0'
    
    // AWS integration
    implementation 'com.javaquery:spring-aws:1.0.0'
}
```

### Using Maven

Add the JLite modules to your `pom.xml`:

```xml
<dependencies>
    <!-- Core utilities -->
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>util</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Spring utilities -->
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>spring</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- CSV processing -->
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>ext-opencsv</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Email support -->
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>spring-email</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- AWS integration -->
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>spring-aws</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## 🏃 Quick Start

### Example: Building a Spring Boot Application with JLite

```java
// 1. Define your entity
@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    // getters and setters
}

// 2. Create repository with specifications
public interface CustomerRepository extends JpaRepository<Customer, Long>,
                                           JpaSpecificationExecutor<Customer>,
                                           AbstractSpecification<Customer> {
}

// 3. Create service extending AbstractService
@Service
public class CustomerServiceImpl extends AbstractService<Customer, Long> 
                                 implements CustomerService {
    
    public CustomerServiceImpl(CustomerRepository repository, 
                              ApplicationEventPublisher eventPublisher) {
        super(repository, eventPublisher);
    }
    
    public List<Customer> findActiveGmailCustomers() {
        Specification<Customer> spec = Specification
            .where(((CustomerRepository) repository).equal("status", "ACTIVE"))
            .and(((CustomerRepository) repository).endsWith("email", "@gmail.com"));
        return findAll(spec);
    }
}

// 4. Use in controller
@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping
    public PageData<Customer> getCustomers(Pageable pageable) {
        return customerService.findAll(pageable);
    }
    
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        Customer saved = customerService.save(customer);
        
        // Send welcome email
        emailService.builder()
            .to(customer.getEmail())
            .subject("Welcome!")
            .htmlBody("<h1>Welcome " + customer.getFirstName() + "!</h1>")
            .send();
        
        return saved;
    }
    
    @PostMapping("/export")
    public void exportToCSV(HttpServletResponse response) {
        List<Customer> customers = customerService.findAll(
            Specification.where(null), Pageable.unpaged()).getData();
        
        CsvWriter.<Customer>builder()
            .headers(List.of("First Name", "Last Name", "Email"))
            .keys(List.of("firstName", "lastName", "email"))
            .data(customers)
            .toOutputStream(response.getOutputStream())
            .write();
    }
}
```

## 🔨 Building from Source

Clone the repository and build using Gradle:

```bash
git clone https://github.com/javaquery/JLite.git
cd JLite
./gradlew build
```

### Build Individual Modules

```bash
# Build core:util
./gradlew :core:util:build

# Build core:spring
./gradlew :core:spring:build

# Build module:ext-opencsv
./gradlew :module:ext-opencsv:build
```

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:spring:test
```

## 📋 Requirements

- **Java**: 11 or higher
- **Spring Boot**: 2.7.18 (for Spring modules)
- **Gradle**: 7.x or higher (for building from source)

## 📖 Documentation

Each module has its own detailed documentation:

- [core:util](core/util/README.md) - Utility functions
- [core:spring](core/spring/README.md) - Spring Boot utilities
- [module:ext-opencsv](module/ext-opencsv/README.md) - CSV processing
- [module:spring-email](module/spring-email/README.md) - Email support
- [module:spring-aws](module/spring-aws/README.md) - AWS integration

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure:
- All tests pass
- Code follows existing style conventions
- New features include tests
- Documentation is updated

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

**javaquery** - [GitHub Profile](https://github.com/javaquery)

## 🙏 Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- CSV processing powered by [OpenCSV](http://opencsv.sourceforge.net/)
- AWS integration using [AWS SDK for Java 2.x](https://aws.amazon.com/sdk-for-java/)

## 📞 Support

- 🐛 [Report Issues](https://github.com/javaquery/JLite/issues)
- 💬 [Discussions](https://github.com/javaquery/JLite/discussions)
- 📧 Contact: [vicky.thakor@javaquery.com](mailto:vicky.thakor@javaquery.com)

---

**Made with ❤️ by javaquery**
