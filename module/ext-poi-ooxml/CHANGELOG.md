# Changelog

All notable changes to the ext-poi-ooxml module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- Multiple sheet support for writing
- Cell styling and formatting (colors, fonts, borders)
- Conditional formatting
- Custom sheet names for writing
- Template-based generation
- Data validation support
- Merge cells support

## [1.0.0] - 2025-01-29

### Added
- Initial release of ext-poi-ooxml library
- Fluent Builder API for both reading and writing Excel files (.xlsx format)
- Streaming support using SXSSFWorkbook for efficient memory management
- Nested object support using dot notation (e.g., `passport.passportNumber`)
- Collection handling for Lists and Sets within data models
- `@Exportable` annotation for field mapping with additional attributes:
  - `isFormula` - Mark field as Excel formula
  - `isRichText` - Mark field for rich text formatting
- Automatic column sizing based on content
- Memory efficient processing:
  - Streaming workbook for writing large datasets
  - Batch processing for reading with configurable batch sizes
- Comprehensive type support:
  - String, Number types (Integer, Long, Double, Float, Short, Byte)
  - Boolean, Date, LocalDateTime
  - Excel formulas and rich text
- `ExcelWriter` with support for:
  - Writing nested objects
  - Writing collections (creates multiple rows per parent object)
  - Optional header row inclusion
  - Automatic type formatting in Excel
  - Formula and rich text cell support
  - Auto column width adjustment
- `ExcelReader` with support for:
  - Row transformation logic with access to headers, current row, and previous row
  - Batch processing with callbacks
  - Skip invalid rows by returning null from transformer
  - Read from specific sheet by index or name
  - Skip rows configuration (for metadata/headers)
  - Access to previous row during transformation
  - Handle multiple cell types (String, Numeric, Boolean, Formula, Date, Blank)
- `BatchProcessor` interface with:
  - `onBatch()` callback for processing each batch
  - `onComplete()` callback with total processed count and batch count
- Advanced collection processing:
  - Automatic row expansion for collection items
  - Handles multiple collections in single object
  - Creates maximum needed rows per parent object
- Comprehensive validation:
  - Headers required when includeHeader is true
  - Headers and keys size validation
  - Proper error messages for configuration issues
- Comprehensive documentation with examples

### Dependencies
- Built on top of Apache POI 5.5.1+
- Requires Java 11 or higher
- Depends on `core:util` module

### Limitations
- Single sheet writing only (sheet named "Sheet1")
- XLSX format only (no legacy .xls support)
- No cell styling or conditional formatting in initial release
- Reading supports multiple sheets, writing does not

[Unreleased]: https://github.com/javaquery/JLite/compare/ext-poi-ooxml-1.0.0...HEAD
[1.0.0]: https://github.com/javaquery/JLite/releases/tag/ext-poi-ooxml-1.0.0
