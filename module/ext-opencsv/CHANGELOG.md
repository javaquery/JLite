# Changelog

All notable changes to the ext-opencsv module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-01-29

### Changed
- Minor updates and improvements

## [1.0.0] - 2025-12-01

### Added
- Initial release of ext-opencsv library
- Fluent Builder API for both reading and writing CSV files
- Batch processing support with configurable batch sizes
- Nested object support using dot notation (e.g., `passport.passportNumber`)
- Collection handling for Lists and Sets within data models
- `@Exportable` annotation for field mapping
- Highly configurable CSV format options:
  - Custom delimiters
  - Custom quote characters
  - Custom escape characters
  - Custom line endings
  - Optional header inclusion
- Row transformation with access to headers, row values, and previous row
- Memory efficient stream processing with batch handling
- `CsvWriter` with support for:
  - Writing nested objects
  - Writing collections (creates multiple rows per parent object)
  - Custom CSV format configuration
- `CsvReader` with support for:
  - Row transformation logic
  - Batch processing with callbacks
  - Skip invalid rows by returning null from transformer
  - Custom CSV format parsing
  - Skip lines configuration
  - Access to previous row during transformation
- `BatchProcessor` interface with:
  - `onBatch()` callback for processing each batch
  - `onComplete()` callback with total processed count and batch count
- Comprehensive documentation and examples

### Dependencies
- Built on top of OpenCSV 5.12.0+
- Requires Java 11 or higher
- Depends on `core:util` module

[1.0.1]: https://github.com/javaquery/JLite/compare/ext-opencsv-1.0.0...ext-opencsv-1.0.1
[1.0.0]: https://github.com/javaquery/JLite/releases/tag/ext-opencsv-1.0.0
