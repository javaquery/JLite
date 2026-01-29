# Changelog

All notable changes to the core:ftpclient module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-01-29

### Changed
- Minor updates and improvements
- Updated dependencies to latest versions
- Upgraded to Java 11

## [1.0.0] - Initial Release

### Added

#### Core Features
- **Multi-Protocol Support** - Unified API for FTP, FTPS, and SFTP
  - `FTPType.FTP` - Standard FTP protocol
  - `FTPType.FTPS` - FTP over SSL/TLS (both explicit and implicit modes)
  - `FTPType.SFTP` - SSH File Transfer Protocol
- **Secure Connections** - Built-in support for encrypted file transfers
  - FTPS with explicit and implicit SSL/TLS modes
  - SFTP over SSH
- **Single Unified Interface** - Consistent API across all protocols via `JFTPClient`

#### File Operations
- `listFiles()` - List files and directories with optional filtering
  - Support for custom `FileFilter<RemoteFile>` implementations
  - Filter by name, size, type, timestamp, or custom criteria
- `uploadFile()` - Upload local files to remote server
  - Boolean return value for success/failure indication
- `downloadFile()` - Download files from remote server to local filesystem
  - Boolean return value for success/failure indication
- `deleteFile()` - Delete files from remote server
  - Boolean return value for success/failure indication

#### Connection Management
- `connect()` - Establish connection to FTP/FTPS/SFTP server
  - Configurable credentials with timeouts
  - Automatic protocol handling
- `disconnect()` - Safely close connection to server
  - Clean resource cleanup

#### Core Classes

##### JFTPClient
- Main client class providing unified FTP operations interface
- Constructor accepts `FTPType` for protocol selection
- Implements all file operations (list, upload, download, delete)
- Connection lifecycle management (connect/disconnect)

##### Credentials (Builder Pattern)
- `host()` - Server hostname configuration
- `port()` - Server port configuration (defaults: FTP=21, SFTP=22, FTPS=990)
- `username()` - Authentication username
- `password()` - Authentication password
- `connectTimeout()` - Connection timeout in milliseconds (default: 15000 ms)
- `socketTimeout()` - Socket operation timeout in milliseconds (default: 60000 ms)
- `isImplicit()` - Implicit SSL mode for FTPS (default: false)
- Builder pattern for clean, readable configuration

##### RemoteFile
- Represents files and directories on remote server
- `getName()` - File/directory name
- `isFile()` - Check if item is a file
- `isDirectory()` - Check if item is a directory
- `getSize()` - File size in bytes
- `getTimestamp()` - Last modification time as Calendar
- `getPath()` - Full path on remote server

##### FileFilter<RemoteFile>
- Functional interface for custom file filtering
- `accept()` - Method to determine if file should be included
- Lambda-friendly design
- Examples provided for common filters:
  - Extension-based filtering
  - Size-based filtering
  - Date/timestamp-based filtering
  - Complex multi-criteria filtering

##### FTPType (Enum)
- `FTP` - Standard File Transfer Protocol
- `SFTP` - SSH File Transfer Protocol
- `FTPS` - FTP over SSL/TLS

#### Implementation Classes
- `FTPClientImpl` - Apache Commons Net FTP implementation
- `FTPSClientImpl` - Apache Commons Net FTPS implementation
- `SFTPClientImpl` - JSch SFTP implementation
- `FileTransferClient` - Internal interface for protocol abstraction

#### Error Handling
- `FTPException` - Custom exception for all FTP-related errors
  - Comprehensive error messages
  - Wraps underlying protocol-specific exceptions
  - Thrown for connection, transfer, and operation failures

#### Logging
- Structured logging via SLF4J
- Logstash encoder support for JSON logging
- Configurable log levels
- Operation tracing and debugging support

#### Configuration Options
- **Connection Timeouts**
  - Default connect timeout: 15 seconds
  - Default socket timeout: 60 seconds
  - Configurable per connection
- **FTPS SSL Modes**
  - Explicit SSL/TLS (default)
  - Implicit SSL/TLS
- **Protocol-Specific Settings**
  - Automatic port selection based on protocol
  - Protocol-appropriate default configurations

### Features

#### File Filtering
- Lambda-based filtering for `listFiles()` operation
- Multiple filter examples in documentation:
  - Filter by file extension (e.g., .pdf, .txt)
  - Filter by file size (minimum/maximum thresholds)
  - Filter by modification date/time
  - Filter directories vs files
  - Composite filters with multiple criteria

#### Batch Operations
- Support for uploading multiple files
- Support for downloading multiple files with filtering
- Retry logic patterns for unreliable connections

#### Best Practices
- Always disconnect pattern with try-finally blocks
- Safe disconnect in error scenarios
- AutoCloseable wrapper pattern for automatic cleanup
- Path validation before operations
- Appropriate timeout configuration guidance
- Retry logic with exponential backoff

### Dependencies
- **Apache Commons Net** 3.12.0 - FTP/FTPS implementation
- **JSch** 2.27.5 - SFTP implementation
- **SLF4J API** 2.0.16 - Logging abstraction
- **Logstash Logback Encoder** 8.0 - Structured logging
- **Logback Classic** 1.5.16 (test) - Logging implementation for tests
- **JUnit Jupiter** 5.8.1 (test) - Unit testing framework
- **MockFtpServer** 3.2.0 (test) - FTP server mocking for tests
- **core:util** - JLite utility classes

### Requirements
- Java 11 or higher

### Documentation
- Comprehensive README with 800+ lines
- Quick start guides for each protocol (FTP, SFTP, FTPS)
- Complete API reference
- Multiple usage examples:
  - Basic connections for all protocols
  - File operations (list, upload, download, delete)
  - Custom file filtering
  - Batch operations
  - Error handling patterns
  - Retry logic
  - Best practices
- Advanced usage patterns
- Custom filter implementations

[1.0.1]: https://github.com/javaquery/JLite/compare/ftpclient-1.0.0...ftpclient-1.0.1
[1.0.0]: https://github.com/javaquery/JLite/releases/tag/ftpclient-1.0.0
