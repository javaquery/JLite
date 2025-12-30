# ext-poi-ooxml

A powerful and flexible Excel extension library built on top of [Apache POI](https://poi.apache.org/), providing enhanced features for writing Excel files with support for nested objects, collections, and automatic column sizing.

## Features

- 🚀 **Fluent Builder API** - Easy-to-use builder pattern for both reading and writing Excel files
- 📦 **Streaming Support** - Efficient memory management using SXSSFWorkbook for large datasets
- 🔗 **Nested Object Support** - Export/import nested objects using dot notation
- 📚 **Collection Handling** - Handle Lists and Sets within your data models
- 🏷️ **Annotation-Based** - Use `@Exportable` annotation for field mapping
- 📏 **Auto Column Sizing** - Automatically adjusts column widths based on content
- ⚙️ **Highly Configurable** - Optional headers, flexible data mapping, and sheet selection
- 💾 **Memory Efficient** - Streaming workbook for writing and batch processing for reading
- 🔄 **Batch Processing** - Read large Excel files with configurable batch sizes
- 📝 **Formula Support** - Write and read Excel formulas
- 🎨 **Rich Text Support** - Handle rich text formatting in cells
- 🔢 **Type Support** - Automatic handling of various data types (String, Number, Boolean, Date, LocalDateTime, etc.)

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:ext-poi-ooxml:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>ext-poi-ooxml</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Writing Excel Files

#### Basic Example

```java
import com.javaquery.poi.excel.writer.ExcelWriter;
import java.io.File;
import java.util.List;

// Create your data objects
List<Customer> customers = getCustomers();

// Write to Excel
ExcelWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Email"))
    .keys(List.of("firstName", "lastName", "email"))
    .data(customers)
    .toFile(new File("customers.xlsx"))
    .write();
```

#### Writing with Nested Objects

```java
ExcelWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Passport Number", "Passport Country"))
    .keys(List.of("firstName", "lastName", "passport.passportNumber", "passport.country"))
    .data(customers)
    .toFile(new File("customers.xlsx"))
    .write();
```

#### Writing with Collections

When your objects contain collections (List or Set), the writer automatically creates multiple rows for each collection item:

```java
ExcelWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Address Line", "City", "State"))
    .keys(List.of("firstName", "lastName", "addresses.addressLine1", "addresses.city", "addresses.state"))
    .data(customers)
    .toFile(new File("customers_addresses.xlsx"))
    .write();
```

**Note**: If a customer has 3 addresses, the writer creates 3 rows for that customer, with the customer's basic information repeated across all rows.

#### Without Header Row

```java
ExcelWriter.<Customer>builder()
    .keys(List.of("firstName", "lastName", "email"))
    .data(customers)
    .toFile(new File("customers.xlsx"))
    .includeHeader(false)
    .write();
```

### Reading Excel Files

#### Basic Example

```java
import com.javaquery.poi.excel.reader.ExcelReader;
import com.javaquery.helper.BatchProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

List<Customer> allCustomers = new ArrayList<>();

ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .rowTransformer((headers, currentRow, previousRow) -> 
        Customer.builder()
            .firstName(getCellValueAsString(currentRow.getCell(0)))
            .lastName(getCellValueAsString(currentRow.getCell(1)))
            .email(getCellValueAsString(currentRow.getCell(2)))
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
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .rowTransformer((headers, currentRow, previousRow) -> 
        Customer.builder()
            .firstName(getCellValueAsString(currentRow.getCell(0)))
            .lastName(getCellValueAsString(currentRow.getCell(1)))
            .email(getCellValueAsString(currentRow.getCell(2)))
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

#### Read from Specific Sheet

```java
// Read by sheet index (0-based)
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .sheetIndex(1)  // Read second sheet
    .rowTransformer(this::transformRow)
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();

// Read by sheet name
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .sheetName("CustomerData")  // Read sheet by name
    .rowTransformer(this::transformRow)
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();
```

#### Skip Invalid Rows

```java
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .rowTransformer((headers, currentRow, previousRow) -> {
        try {
            return Customer.builder()
                .firstName(getCellValueAsString(currentRow.getCell(0)))
                .lastName(getCellValueAsString(currentRow.getCell(1)))
                .age(getCellValueAsInt(currentRow.getCell(2)))
                .build();
        } catch (NumberFormatException e) {
            // Return null to skip invalid rows
            return null;
        }
    })
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .read();
```

#### Skip Header or Metadata Rows

```java
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .skipRows(2)  // Skip first 2 rows (e.g., comments or metadata)
    .rowTransformer(this::transformRow)
    .batchProcessor(batch -> allCustomers.addAll(batch))
    .batchSize(2000)
    .read();
```

#### Access Previous Row During Transformation

```java
ExcelReader.<Customer>builder()
    .source(new File("customers.xlsx"))
    .rowTransformer((headers, currentRow, previousRow) -> {
        Customer customer = Customer.builder()
            .firstName(getCellValueAsString(currentRow.getCell(0)))
            .lastName(getCellValueAsString(currentRow.getCell(1)))
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

Use the `@Exportable` annotation to mark fields for Excel export:

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

### Formulas and Rich Text

```java
public class Product {
    @Exportable(key = "name")
    private String name;

    @Exportable(key = "description", isRichText = true)
    private String description;  // Will be rendered as rich text in Excel

    @Exportable(key = "price")
    private Double price;

    @Exportable(key = "quantity")
    private Integer quantity;

    @Exportable(key = "totalPrice", isFormula = true)
    private String totalPrice;  // Excel formula (e.g., "B2*C2")
    
    // Getters and setters
}
```

#### Writing with Formulas

```java
Product product = Product.builder()
    .name("Laptop")
    .price(999.99)
    .quantity(5)
    .totalPrice("B2*C2")  // Formula to multiply price * quantity
    .build();

ExcelWriter.<Product>builder()
    .headers(List.of("Name", "Price", "Quantity", "Total Price"))
    .keys(List.of("name", "price", "quantity", "totalPrice"))
    .data(List.of(product))
    .toFile(new File("products.xlsx"))
    .write();
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

### Writing Large Datasets

The library uses `SXSSFWorkbook` (Streaming Workbook) which keeps a configurable number of rows in memory, making it suitable for large datasets:

```java
// Write 10,000 records efficiently
ExcelWriter.<Customer>builder()
    .headers(List.of("First Name", "Last Name", "Email"))
    .keys(List.of("firstName", "lastName", "email"))
    .data(customerRepository.findAll()) // Large dataset
    .toFile(new File("large_customers.xlsx"))
    .write();
```

### Complex Nested Structures

```java
// Export customer with multiple nested levels
ExcelWriter.<Customer>builder()
    .headers(List.of(
        "First Name", 
        "Last Name", 
        "Passport Number", 
        "Passport Country",
        "Address Line 1",
        "City",
        "State"
    ))
    .keys(List.of(
        "firstName", 
        "lastName", 
        "passport.passportNumber",
        "passport.country",
        "addresses.addressLine1",
        "addresses.city",
        "addresses.state"
    ))
    .data(customers)
    .toFile(new File("customers_detailed.xlsx"))
    .write();
```

### Handling Multiple Collections

When a single object contains multiple collection items, the writer creates the maximum number of rows needed:

```java
// Customer with 4 addresses will create 4 rows
// Each row shows the same customer info with different address details
List<Customer> customers = getCustomers(); // Each customer has 0-N addresses

ExcelWriter.<Customer>builder()
    .headers(List.of("Name", "Email", "Address", "City"))
    .keys(List.of("firstName", "email", "addresses.addressLine1", "addresses.city"))
    .data(customers)
    .toFile(new File("customers_all_addresses.xlsx"))
    .write();
```

### Type Support

The writer automatically handles different data types:

```java
public class Employee {
    @Exportable(key = "name")
    private String name;              // String
    
    @Exportable(key = "salary")
    private Double salary;            // Double
    
    @Exportable(key = "bonus")
    private Float bonus;              // Float
    
    @Exportable(key = "employeeId")
    private Long employeeId;          // Long
    
    @Exportable(key = "age")
    private Integer age;              // Integer
    
    @Exportable(key = "rating")
    private Byte rating;              // Byte
    
    @Exportable(key = "views")
    private Short views;              // Short
    
    @Exportable(key = "active")
    private Boolean active;           // Boolean
    
    @Exportable(key = "hireDate")
    private Date hireDate;            // Date
    
    @Exportable(key = "lastLogin")
    private LocalDateTime lastLogin;  // LocalDateTime
    
    @Exportable(key = "totalBonus", isFormula = true)
    private String totalBonus;        // Formula
    
    @Exportable(key = "notes", isRichText = true)
    private String notes;             // Rich Text
}

// All types are properly formatted in Excel
ExcelWriter.<Employee>builder()
    .headers(List.of("Name", "Salary", "Active", "Hire Date", "Last Login", "Total Bonus", "Notes"))
    .keys(List.of("name", "salary", "active", "hireDate", "lastLogin", "totalBonus", "notes"))
    .data(employees)
    .toFile(new File("employees.xlsx"))
    .write();
```

#### Reading Different Cell Types

```java
// Helper method to read different cell types
public static String getCellValueAsString(Cell cell) {
    if (cell == null) return "";
    
    switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            }
            return String.valueOf(cell.getNumericCellValue());
        case BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        case FORMULA:
            return cell.getCellFormula();
        case BLANK:
            return "";
        default:
            return "";
    }
}

public static Integer getCellValueAsInt(Cell cell) {
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) {
        return (int) cell.getNumericCellValue();
    }
    return null;
}
```

### Error Handling

```java
try {
    ExcelWriter.<Customer>builder()
        .headers(List.of("First Name", "Last Name", "Email"))
        .keys(List.of("firstName", "lastName", "email"))
        .data(customers)
        .toFile(new File("customers.xlsx"))
        .write();
} catch (IOException e) {
    System.err.println("Failed to write Excel: " + e.getMessage());
} catch (IllegalArgumentException e) {
    System.err.println("Configuration error: " + e.getMessage());
}
```

### Validation Examples

```java
// Missing headers when includeHeader is true
try {
    ExcelWriter.<Customer>builder()
        .keys(List.of("firstName", "lastName"))
        .data(customers)
        .toFile(new File("customers.xlsx"))
        .includeHeader(true)  // But no headers provided!
        .write();
} catch (IllegalArgumentException e) {
    // Exception: "Headers must be provided when includeHeader is true"
}

// Headers and keys size mismatch
try {
    ExcelWriter.<Customer>builder()
        .headers(List.of("First Name", "Last Name", "Email"))  // 3 headers
        .keys(List.of("firstName", "lastName"))                 // 2 keys
        .data(customers)
        .toFile(new File("customers.xlsx"))
        .write();
} catch (IllegalArgumentException e) {
    // Exception: "Headers and Keys size must be same"
}
```

## Configuration Options

### ExcelWriter Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `headers` | `List<String>` | Required (if includeHeader=true) | Column headers |
| `keys` | `List<String>` | Required | Field keys (supports dot notation) |
| `data` | `Iterable<T>` | Required | Data to write |
| `toFile` | `File` | Required | Destination file |
| `includeHeader` | `boolean` | `true` | Include header row |

### ExcelReader Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `source` | `File` | Required | Source Excel file |
| `rowTransformer` | `ExcelRowTransformer<T>` | Required | Row transformation function |
| `batchProcessor` | `BatchProcessor<T>` | Required | Batch processing handler |
| `sheetIndex` | `int` | `0` | Sheet index to read (0-based) |
| `sheetName` | `String` | `null` | Sheet name to read (overrides sheetIndex) |
| `skipRows` | `int` | `0` | Number of rows to skip at the beginning |
| `batchSize` | `int` | `1000` | Records per batch |

## How It Works

### Key Resolution

The writer uses the `@Exportable` annotation to map keys to fields:

1. For simple fields: `"firstName"` → finds field with `@Exportable(key = "firstName")`
2. For nested objects: `"passport.country"` → finds passport field, then country field within it
3. For collections: `"addresses.city"` → finds addresses collection, then city field within each Address

### Collection Processing

When a key references a collection:
- The writer determines the maximum collection size for each data item
- Creates that many rows for the item
- Fills collection values row by row
- Empty strings for missing values (when collection size < max size)

### Column Width

Columns are automatically sized based on:
- Header text length
- Content length in all rows
- Padding of 2 characters added for readability
- Width calculation: `(maxLength + 2) * 256` Excel units

### Memory Management

- Uses `SXSSFWorkbook` with a default window of 100 rows
- Only keeps 100 rows in memory at a time
- Older rows are flushed to disk automatically
- Suitable for files with millions of rows

## Performance Tips

1. **Large Datasets (Writing)**: The library is optimized for large datasets using streaming workbook
   - Can handle millions of rows efficiently
   - Memory footprint remains constant

2. **Large Files (Reading)**: Choose appropriate batch size based on your memory constraints
   - For large files: 500-1000 records per batch
   - For small files: 5000-10000 records per batch
   - Memory management: The reader processes files in batches to avoid loading entire file into memory

3. **Collection Size**: Be aware that collections create multiple rows
   - 1 customer with 10 addresses = 10 rows in Excel
   - 100 customers with avg 5 addresses = ~500 rows

4. **Field Access**: Use public fields or proper getters for best performance

5. **File Format**: Library supports `.xlsx` files (Office 2007+ format)

6. **Null Transformers**: Return `null` from `rowTransformer` to skip invalid rows without throwing exceptions

## Limitations

- **Single Sheet Writing**: Currently creates files with a single sheet named "Sheet1" (reading supports multiple sheets)
- **XLSX Format**: Only supports `.xlsx` format (not `.xls` legacy format)
- **No Styling**: Does not support cell styling, colors, or formatting (coming in future versions)
- **No Conditional Formatting**: Conditional formatting support is planned but not yet implemented

## Roadmap

Completed features:

- [x] Read support for Excel files
- [x] Formula support
- [x] Rich text support
- [x] Multiple sheet support (reading)

Future enhancements planned:

- [ ] Multiple sheet support (writing)
- [ ] Cell styling and formatting
- [ ] Conditional formatting
- [ ] Custom sheet names (writing)
- [ ] Template-based generation
- [ ] Data validation support
- [ ] Merge cells support

## Requirements

- Java 11 or higher
- Apache POI 5.5.1 or higher

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

