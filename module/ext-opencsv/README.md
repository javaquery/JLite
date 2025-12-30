# ext-opencsv

A powerful and flexible CSV extension library built on top of [OpenCSV](http://opencsv.sourceforge.net/), providing enhanced features for reading and writing CSV files with support for batch processing, nested objects, and collections.

## Features

- 🚀 **Fluent Builder API** - Easy-to-use builder pattern for both reading and writing
- 📦 **Batch Processing** - Efficient memory management with configurable batch sizes
- 🔗 **Nested Object Support** - Export/import nested objects using dot notation
- 📚 **Collection Handling** - Handle Lists and Sets within your data models
- 🏷️ **Annotation-Based** - Use `@Exportable` annotation for field mapping
- ⚙️ **Highly Configurable** - Custom delimiters, quote characters, and escape characters
- 🔄 **Row Transformation** - Transform CSV rows into Java objects with custom logic
- 💾 **Memory Efficient** - Stream processing with batch handling for large files

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:ext-opencsv:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>ext-opencsv</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Writing CSV Files

#### Basic Example

```java
import com.javaquery.opencsv.writer.CsvWriter;
import java.io.File;
import java.util.List;

// Create your data objects
List<Customer> customers = getCustomers();

// Write to CSV
CsvWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Email"))
    .keys(List.of("firstName", "lastName", "email"))
    .data(customers)
    .toFile(new File("customers.csv"))
    .write();
```

#### Writing with Nested Objects

```java
CsvWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Passport Number", "Passport Country"))
    .keys(List.of("firstName", "lastName", "passport.passportNumber", "passport.country"))
    .data(customers)
    .toFile(new File("customers.csv"))
    .write();
```

#### Writing with Collections

When your objects contain collections (List or Set), the writer automatically creates multiple rows for each collection item:

```java
CsvWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Address Line", "City", "State"))
    .keys(List.of("firstName", "lastName", "addresses.addressLine1", "addresses.city", "addresses.state"))
    .data(customers)
    .toFile(new File("customers_addresses.csv"))
    .write();
```

#### Custom CSV Format

```java
CsvWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Email"))
    .keys(List.of("firstName", "lastName", "email"))
    .data(customers)
    .toFile(new File("customers.csv"))
    .delimiter('|')              // Pipe-delimited
    .quoteChar('\'')             // Single quote
    .escapeChar('\\')            // Backslash escape
    .lineEnd("\r\n")             // Windows line ending
    .includeHeader(false)        // Exclude header row
    .write();
```

### Reading CSV Files

#### Basic Example

```java
import com.javaquery.opencsv.reader.CsvReader;
import com.javaquery.helper.BatchProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

List<Customer> allCustomers = new ArrayList<>();

CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, rowValues, previousRow) -> 
        Customer.builder()
            .firstName(rowValues[0])
            .lastName(rowValues[1])
            .email(rowValues[2])
            .build()
    )
    .batchProcessor(batch -> {
        allCustomers.addAll(batch);
        // Or process batch (e.g., save to database)
    })
    .batchSize(1000)
    .read();
```

#### With Completion Callback

```java
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, rowValues, previousRow) -> 
        Customer.builder()
            .firstName(rowValues[0])
            .lastName(rowValues[1])
            .email(rowValues[2])
            .build()
    )
    .batchProcessor(new BatchProcessor<Customer>() {
        @Override
        public void onBatch(List<Customer> batch) {
            // Process each batch
            System.out.println("Processing batch of " + batch.size() + " customers");
            customerRepository.saveAll(batch);
        }

        @Override
        public void onComplete(int totalProcessed, int totalBatches) {
            System.out.println("Processed " + totalProcessed + " records in " + totalBatches + " batches");
        }
    })
    .batchSize(500)
    .read();
```

#### Skip Invalid Rows

```java
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, rowValues, previousRow) -> {
        try {
            return Customer.builder()
                .firstName(rowValues[0])
                .lastName(rowValues[1])
                .age(Integer.parseInt(rowValues[2]))
                .build();
        } catch (NumberFormatException e) {
            // Return null to skip invalid rows
            return null;
        }
    })
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();
```

#### Custom CSV Format

```java
CsvReader.<Customer>builder()
    .source(new File("customers.tsv"))
    .delimiter('\t')             // Tab-delimited
    .quoteChar('\'')             // Single quote
    .escapeChar('\\')            // Backslash escape
    .skipLines(1)                // Skip first line (e.g., metadata)
    .rowTransformer((headers, rowValues, previousRow) -> 
        Customer.builder()
            .firstName(rowValues[0])
            .lastName(rowValues[1])
            .build()
    )
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .batchSize(2000)
    .read();
```

#### Access Previous Row During Transformation

```java
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, rowValues, previousRow) -> {
        Customer customer = Customer.builder()
            .firstName(rowValues[0])
            .lastName(rowValues[1])
            .build();
        
        // Access previous row for context
        if (previousRow != null) {
            // Use previous row data for processing
            customer.setSameAddressAsPrevious(true);
        }
        
        return customer;
    })
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();
```

## Data Model Setup

Use the `@Exportable` annotation to mark fields for CSV export:

```java
import com.javaquery.annotations.Exportable;

public class Customer {
    @Exportable(key = "firstName")
    private String firstName;

    @Exportable(key = "lastName")
    private String lastName;

    @Exportable(key = "email")
    private String email;

    @Exportable(key = "age")
    private Integer age;

    @Exportable(key = "passport")
    private Passport passport;

    @Exportable(key = "addresses")
    private Set<Address> addresses;

    // Getters and setters
}
```

### Nested Object Example

```java
public class Passport {
    @Exportable(key = "passportNumber")
    private String passportNumber;

    @Exportable(key = "country")
    private String country;

    @Exportable(key = "expirationDate")
    private String expirationDate;
}
```

### Collection Example

```java
public class Address {
    @Exportable(key = "addressLine1")
    private String addressLine1;

    @Exportable(key = "city")
    private String city;

    @Exportable(key = "state")
    private String state;

    @Exportable(key = "zipCode")
    private String zipCode;
}
```

## Advanced Usage

### Custom Batch Processing Strategy

```java
class DatabaseBatchProcessor implements BatchProcessor<Customer> {
    private final CustomerRepository repository;
    private final int commitThreshold;
    private int totalSaved = 0;

    @Override
    public void onBatch(List<Customer> batch) {
        repository.saveAll(batch);
        totalSaved += batch.size();
        
        if (totalSaved >= commitThreshold) {
            repository.flush();
            totalSaved = 0;
        }
    }

    @Override
    public void onComplete(int totalProcessed, int totalBatches) {
        repository.flush(); // Final flush
        System.out.println("Import complete: " + totalProcessed + " records");
    }
}

// Usage
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer(this::transformRow)
    .batchProcessor(new DatabaseBatchProcessor(customerRepository, 10000))
    .batchSize(1000)
    .read();
```

### Dynamic Header Mapping

```java
CsvReader.<Customer>builder()
    .source(new File("customers.csv"))
    .rowTransformer((headers, rowValues, previousRow) -> {
        Customer customer = new Customer();
        
        // Find column index dynamically
        for (int i = 0; i < headers.length; i++) {
            switch (headers[i].toLowerCase()) {
                case "first name":
                case "firstname":
                    customer.setFirstName(rowValues[i]);
                    break;
                case "last name":
                case "lastname":
                    customer.setLastName(rowValues[i]);
                    break;
                case "email":
                case "email address":
                    customer.setEmail(rowValues[i]);
                    break;
            }
        }
        
        return customer;
    })
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();
```

### Error Handling

```java
try {
    CsvWriter.<Customer>builder()
        .headers(List.of("First Name", "Last Name", "Email"))
        .keys(List.of("firstName", "lastName", "email"))
        .data(customers)
        .toFile(new File("customers.csv"))
        .write();
} catch (IOException e) {
    System.err.println("Failed to write CSV: " + e.getMessage());
}

try {
    CsvReader.<Customer>builder()
        .source(new File("customers.csv"))
        .rowTransformer(this::transformRow)
        .batchProcessor(this::processBatch)
        .read();
} catch (IOException e) {
    System.err.println("Failed to read CSV: " + e.getMessage());
} catch (IllegalArgumentException e) {
    System.err.println("Configuration error: " + e.getMessage());
}
```

## Configuration Options

### CsvWriter Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `headers` | `List<String>` | Required (if includeHeader=true) | Column headers |
| `keys` | `List<String>` | Required | Field keys (supports dot notation) |
| `data` | `Iterable<T>` | Required | Data to write |
| `toFile` | `File` | Required | Destination file |
| `delimiter` | `char` | `,` | Field delimiter |
| `quoteChar` | `char` | `"` | Quote character |
| `escapeChar` | `char` | `"` | Escape character |
| `lineEnd` | `String` | `\n` | Line ending |
| `includeHeader` | `boolean` | `true` | Include header row |

### CsvReader Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `source` | `File` | Required | Source CSV file |
| `rowTransformer` | `CsvRowTransformer<T>` | Required | Row transformation function |
| `batchProcessor` | `BatchProcessor<T>` | Required | Batch processing handler |
| `delimiter` | `char` | `,` | Field delimiter |
| `quoteChar` | `char` | `"` | Quote character |
| `escapeChar` | `char` | `"` | Escape character |
| `skipLines` | `int` | `0` | Number of lines to skip |
| `batchSize` | `int` | `1000` | Records per batch |

## Performance Tips

1. **Batch Size**: Choose appropriate batch size based on your memory constraints
   - For large files: 500-1000 records
   - For small files: 5000-10000 records

2. **Memory Management**: The reader processes files in batches to avoid loading entire file into memory

3. **Collection Handling**: Be aware that writing collections creates multiple rows per parent object

4. **Null Transformers**: Return `null` from `rowTransformer` to skip invalid rows without throwing exceptions

## Requirements

- Java 11 or higher
- OpenCSV 5.12.0 or higher

## License

This library is part of the JLite project. See LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or contributions, please visit the [GitHub repository](https://github.com/javaquery/JLite).

## Author

**Vicky Thakor**  
JavaQuery

---

**Version**: 1.0.0  
**Last Updated**: December 2025

