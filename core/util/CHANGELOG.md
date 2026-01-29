# Changelog

All notable changes to the core:util module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.8] - 2026-01-29

### Changed
- Exportable interface for CSV/Excel export enhanced to support formula and rich text fields.
- AccessRefreshToken dto added.
- Upgraded to Java 11.

## [1.2.7] - Earlier Release

### Changed
- Bug fixes and enhancements

## [1.2.0] - Earlier Release

### Added
- Enhanced utility functionality

## [1.1.0] - Earlier Release

### Added
- Additional utility methods

## [1.0.0] - Initial Release

### Added

#### Annotations
- `@Exportable` - Annotation for marking fields for CSV/Excel export with support for:
  - Custom key mapping
  - Formula field support (`isFormula`)
  - Rich text field support (`isRichText`)

#### Core Utilities
- `Assert` - Assertion utility methods with exception throwing
  - `nonNull()` - Assert object is not null
  - `nonNullNonEmpty()` - Assert collections/strings are not null or empty
  - `nullOrEmpty()` - Assert collections/strings/maps are null or empty
  - Custom exception supplier support
- `ExecutableFunction` - Functional interface for executable code blocks
- `ExecutionContext` - Context holder for execution state
- `Is` - Wrapper class for validation checks
  - `isNull()` / `nonNull()` - Null checking with optional executable functions
  - `nullOrEmpty()` / `nonNullNonEmpty()` - String, Collection, Iterable, and Map validation
  - `email()` - Email validation
  - Executable function support for conditional execution

#### String Utilities
- `Strings` - String manipulation and validation
  - `nullOrEmpty()` / `nonNullNonEmpty()` - String validation
  - String manipulation methods
  - Safe string operations
- `Regex` - Regular expression utilities
  - Pattern matching and validation
  - Common regex patterns

#### Date and Time Utilities
- `Dates` - Date manipulation and formatting
  - `parse()` - Parse strings to Date with various patterns
  - `format()` - Format Date to string
  - Date arithmetic operations
- `LocalDates` - LocalDate/LocalDateTime utilities
  - Modern Java date/time API support
  - Date conversion and manipulation
- `DateRange` - Date range representation and operations
  - Range validation and checks
  - Range queries
- `DatePattern` - Predefined date format patterns
  - `YYYY_MM_DD` and other common patterns
  - Standard date format constants
- `DateTimeFormat` - Date/time formatting utilities

#### Number Utilities
- `Numbers` - Number manipulation and validation
  - Type conversion utilities
  - Number validation methods
  - Safe number operations

#### Collection Utilities
- `Collections` - Extended collection utilities
  - `singleton()` / `singletonList()` / `singletonMap()` - Create immutable single-element collections
  - `emptySet()` / `emptyList()` / `emptyMap()` - Create empty immutable collections
  - `nullOrEmpty()` / `nonNullNonEmpty()` - Collection validation
  - Collection comparison utilities
  - Cardinality operations (frequency counting)
  - `CardinalityHelper` - Internal helper for collection cardinality operations
- `Arrays` - Array manipulation utilities
  - Array operations and transformations

#### JSON Utilities
- `JSON` - JSON processing and manipulation
  - JSON parsing and serialization
  - JSON object/array manipulation
  - Integration with org.json library

#### File Utilities
- `Files` - File operation utilities
  - File manipulation methods
  - Safe file operations
- `FileTypes` - File type detection and validation
  - Common file type constants
  - File extension handling

#### HTTP Utilities
- `CommonResponse` - Standard HTTP response wrapper
  - Response status handling
  - Response data encapsulation
- `HttpStatusCode` - HTTP status code constants
  - Standard HTTP status codes
  - Status code validation

#### Security Utilities
- `SecurityProvider` - Security provider utilities
  - Security configuration support
- `UniqueIdGenerator` - Generate unique identifiers
  - UUID generation
  - Custom ID generation strategies

#### Logging Utilities
- `LogBuilder` - Fluent logging builder
  - Structured logging support
  - Log message building with context
  - Integration with SLF4J

#### Batch Processing
- `BatchProcessor<T>` - Interface for batch processing operations
  - `onBatch()` - Process batch callback
  - `onComplete()` - Completion callback with statistics

#### Helper Utilities
- `AccessRefreshToken` - Token management for OAuth/JWT
  - Access token and refresh token handling

#### Enums and Constants
- `Gender` - Gender enumeration
- `MaritalStatus` - Marital status enumeration
- `PaymentStatus` - Payment status enumeration
- `Action` - Action type enumeration
- `ActivityStatus` - Activity status enumeration

### Dependencies
- SLF4J API 2.0.16 (compileOnly)
- org.json 20250517 (compileOnly)
- Jackson Annotations 2.18.2 (compileOnly)
- Logstash Logback Encoder 7.4 (test)
- Logback Classic 1.3.15 (test)

### Requirements
- Java 11 or higher

[1.2.8]: https://github.com/javaquery/JLite/compare/util-1.2.7...util-1.2.8
[1.2.7]: https://github.com/javaquery/JLite/compare/util-1.2.0...util-1.2.7
[1.2.0]: https://github.com/javaquery/JLite/compare/util-1.1.0...util-1.2.0
[1.1.0]: https://github.com/javaquery/JLite/compare/util-1.0.0...util-1.1.0
[1.0.0]: https://github.com/javaquery/JLite/releases/tag/util-1.0.0
