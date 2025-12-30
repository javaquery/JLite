# FTP Client

A unified FTP client library for Java projects that provides seamless support for FTP, FTPS (FTP over SSL/TLS), and SFTP (SSH File Transfer Protocol) through a single, consistent API. Built on Apache Commons Net and JSch libraries.

## Features

- 🌐 **Multi-Protocol Support** - FTP, FTPS, and SFTP with unified interface
- 🔐 **Secure Connections** - Built-in support for FTPS and SFTP
- 📂 **File Operations** - Upload, download, delete, and list files
- 🔍 **File Filtering** - Custom filters for selective file listing
- ⚙️ **Configurable** - Custom timeouts, ports, and connection settings
- 🛡️ **Robust Error Handling** - Comprehensive exception handling with FTPException
- 📝 **Structured Logging** - SLF4J integration with Logstash support
- 🔄 **Connection Management** - Easy connect and disconnect operations

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:ftpclient:1.0.1'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>ftpclient</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Quick Start

### Basic FTP Connection

```java
import com.javaquery.ftp.*;
import com.javaquery.ftp.io.RemoteFile;
import java.util.List;

// Create FTP client
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);

// Configure credentials
Credentials credentials = Credentials.builder()
    .host("ftp.example.com")
    .port(21)
    .username("username")
    .password("password")
    .build();

// Connect
ftpClient.connect(credentials);

// Perform operations
List<RemoteFile> files = ftpClient.listFiles("/remote/path", null);

// Disconnect
ftpClient.disconnect();
```

### SFTP Connection

```java
// Create SFTP client
JFTPClient sftpClient = new JFTPClient(FTPType.SFTP);

// Configure credentials
Credentials credentials = Credentials.builder()
    .host("sftp.example.com")
    .port(22)
    .username("username")
    .password("password")
    .build();

// Connect and use
sftpClient.connect(credentials);
List<RemoteFile> files = sftpClient.listFiles("/home/user", null);
sftpClient.disconnect();
```

### FTPS Connection

```java
// Create FTPS client (FTP over SSL/TLS)
JFTPClient ftpsClient = new JFTPClient(FTPType.FTPS);

// Configure credentials
Credentials credentials = Credentials.builder()
    .host("ftps.example.com")
    .port(990)
    .username("username")
    .password("password")
    .isImplicit(true)  // Use implicit SSL
    .build();

// Connect and use
ftpsClient.connect(credentials);
List<RemoteFile> files = ftpsClient.listFiles("/secure/path", null);
ftpsClient.disconnect();
```

## Supported Protocols

### FTP (File Transfer Protocol)

Standard FTP protocol for file transfers.

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);

Credentials credentials = Credentials.builder()
    .host("ftp.example.com")
    .port(21)
    .username("user")
    .password("pass")
    .build();

ftpClient.connect(credentials);
// Perform operations
ftpClient.disconnect();
```

### SFTP (SSH File Transfer Protocol)

Secure file transfer over SSH.

```java
JFTPClient sftpClient = new JFTPClient(FTPType.SFTP);

Credentials credentials = Credentials.builder()
    .host("sftp.example.com")
    .port(22)
    .username("user")
    .password("pass")
    .build();

sftpClient.connect(credentials);
// Perform operations
sftpClient.disconnect();
```

### FTPS (FTP over SSL/TLS)

Secure FTP with SSL/TLS encryption.

```java
JFTPClient ftpsClient = new JFTPClient(FTPType.FTPS);

// Explicit FTPS (default)
Credentials credentials = Credentials.builder()
    .host("ftps.example.com")
    .port(21)
    .username("user")
    .password("pass")
    .isImplicit(false)
    .build();

// Implicit FTPS
Credentials implicitCredentials = Credentials.builder()
    .host("ftps.example.com")
    .port(990)
    .username("user")
    .password("pass")
    .isImplicit(true)
    .build();

ftpsClient.connect(credentials);
// Perform operations
ftpsClient.disconnect();
```

## File Operations

### List Files

List all files in a directory:

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
ftpClient.connect(credentials);

// List all files
List<RemoteFile> files = ftpClient.listFiles("/remote/directory", null);

for (RemoteFile file : files) {
    System.out.println("Name: " + file.getName());
    System.out.println("Size: " + file.getSize());
    System.out.println("Is File: " + file.isFile());
    System.out.println("Is Directory: " + file.isDirectory());
    System.out.println("Path: " + file.getPath());
    System.out.println("Timestamp: " + file.getTimestamp().getTime());
}

ftpClient.disconnect();
```

### List Files with Filter

Filter files based on custom criteria:

```java
// Filter for PDF files only
FileFilter<RemoteFile> pdfFilter = file -> 
    file.isFile() && file.getName().endsWith(".pdf");

List<RemoteFile> pdfFiles = ftpClient.listFiles("/documents", pdfFilter);

// Filter for files larger than 1MB
FileFilter<RemoteFile> largeFileFilter = file -> 
    file.isFile() && file.getSize() > 1024 * 1024;

List<RemoteFile> largeFiles = ftpClient.listFiles("/uploads", largeFileFilter);

// Filter for directories only
FileFilter<RemoteFile> directoryFilter = RemoteFile::isDirectory;

List<RemoteFile> directories = ftpClient.listFiles("/", directoryFilter);

// Complex filter
FileFilter<RemoteFile> complexFilter = file -> {
    if (!file.isFile()) return false;
    String name = file.getName().toLowerCase();
    return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif");
};

List<RemoteFile> imageFiles = ftpClient.listFiles("/images", complexFilter);
```

### Upload File

Upload a local file to remote server:

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
ftpClient.connect(credentials);

// Upload file
boolean success = ftpClient.uploadFile(
    "/local/path/document.pdf",
    "/remote/path/document.pdf"
);

if (success) {
    System.out.println("File uploaded successfully");
} else {
    System.out.println("File upload failed");
}

ftpClient.disconnect();
```

### Download File

Download a file from remote server to local:

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
ftpClient.connect(credentials);

// Download file
boolean success = ftpClient.downloadFile(
    "/remote/path/report.pdf",
    "/local/path/report.pdf"
);

if (success) {
    System.out.println("File downloaded successfully");
} else {
    System.out.println("File download failed");
}

ftpClient.disconnect();
```

### Delete File

Delete a file from remote server:

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
ftpClient.connect(credentials);

// Delete file
boolean success = ftpClient.deleteFile("/remote/path/old-file.txt");

if (success) {
    System.out.println("File deleted successfully");
} else {
    System.out.println("File deletion failed");
}

ftpClient.disconnect();
```

## Credentials Configuration

### Basic Credentials

```java
Credentials credentials = Credentials.builder()
    .host("ftp.example.com")
    .port(21)
    .username("user")
    .password("password")
    .build();
```

### Custom Timeouts

```java
Credentials credentials = Credentials.builder()
    .host("ftp.example.com")
    .port(21)
    .username("user")
    .password("password")
    .connectTimeout(30000)  // 30 seconds
    .socketTimeout(120000)   // 120 seconds
    .build();
```

### FTPS with Implicit SSL

```java
Credentials credentials = Credentials.builder()
    .host("ftps.example.com")
    .port(990)
    .username("user")
    .password("password")
    .isImplicit(true)  // Use implicit SSL/TLS
    .build();
```

### Default Values

The `Credentials` class provides sensible defaults:
- **connectTimeout**: 15000 ms (15 seconds)
- **socketTimeout**: 60000 ms (60 seconds)
- **isImplicit**: false (explicit SSL/TLS for FTPS)

## Advanced Usage

### Complete File Transfer Example

```java
import com.javaquery.ftp.*;
import com.javaquery.ftp.exception.FTPException;
import com.javaquery.ftp.io.RemoteFile;
import java.util.List;

public class FileTransferExample {
    
    public static void main(String[] args) {
        JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
        
        try {
            // Configure connection
            Credentials credentials = Credentials.builder()
                .host("ftp.example.com")
                .port(21)
                .username("user")
                .password("password")
                .connectTimeout(20000)
                .socketTimeout(90000)
                .build();
            
            // Connect
            ftpClient.connect(credentials);
            System.out.println("Connected successfully");
            
            // List files
            List<RemoteFile> files = ftpClient.listFiles("/uploads", 
                file -> file.isFile() && file.getName().endsWith(".txt"));
            
            System.out.println("Found " + files.size() + " text files");
            
            // Upload new file
            boolean uploaded = ftpClient.uploadFile(
                "/local/new-document.pdf",
                "/uploads/new-document.pdf"
            );
            
            if (uploaded) {
                System.out.println("File uploaded successfully");
            }
            
            // Download file
            boolean downloaded = ftpClient.downloadFile(
                "/uploads/report.pdf",
                "/local/downloads/report.pdf"
            );
            
            if (downloaded) {
                System.out.println("File downloaded successfully");
            }
            
            // Delete old file
            boolean deleted = ftpClient.deleteFile("/uploads/old-file.txt");
            
            if (deleted) {
                System.out.println("File deleted successfully");
            }
            
        } catch (FTPException e) {
            System.err.println("FTP operation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always disconnect
            try {
                ftpClient.disconnect();
                System.out.println("Disconnected successfully");
            } catch (FTPException e) {
                System.err.println("Error disconnecting: " + e.getMessage());
            }
        }
    }
}
```

### Batch File Upload

```java
import java.io.File;
import java.util.Arrays;

public void uploadMultipleFiles(JFTPClient ftpClient, String localDir, String remoteDir) {
    File directory = new File(localDir);
    File[] files = directory.listFiles();
    
    if (files != null) {
        for (File file : files) {
            if (file.isFile()) {
                String localPath = file.getAbsolutePath();
                String remotePath = remoteDir + "/" + file.getName();
                
                try {
                    boolean success = ftpClient.uploadFile(localPath, remotePath);
                    if (success) {
                        System.out.println("Uploaded: " + file.getName());
                    } else {
                        System.err.println("Failed to upload: " + file.getName());
                    }
                } catch (FTPException e) {
                    System.err.println("Error uploading " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
}
```

### Batch File Download

```java
public void downloadFilteredFiles(JFTPClient ftpClient, String remoteDir, String localDir) {
    try {
        // Filter for files modified in last 7 days
        long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        
        FileFilter<RemoteFile> recentFilesFilter = file -> 
            file.isFile() && file.getTimestamp().getTimeInMillis() > sevenDaysAgo;
        
        List<RemoteFile> recentFiles = ftpClient.listFiles(remoteDir, recentFilesFilter);
        
        for (RemoteFile file : recentFiles) {
            String localPath = localDir + File.separator + file.getName();
            boolean success = ftpClient.downloadFile(file.getPath(), localPath);
            
            if (success) {
                System.out.println("Downloaded: " + file.getName());
            }
        }
    } catch (FTPException e) {
        System.err.println("Error during batch download: " + e.getMessage());
    }
}
```

### Working with Different Protocols

```java
public class MultiProtocolClient {
    
    public JFTPClient createClient(String protocol, Credentials credentials) throws FTPException {
        FTPType ftpType;
        
        switch (protocol.toUpperCase()) {
            case "FTP":
                ftpType = FTPType.FTP;
                break;
            case "SFTP":
                ftpType = FTPType.SFTP;
                break;
            case "FTPS":
                ftpType = FTPType.FTPS;
                break;
            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
        
        JFTPClient client = new JFTPClient(ftpType);
        client.connect(credentials);
        return client;
    }
    
    public void transferFile(String protocol, Credentials credentials, 
                           String localFile, String remoteFile) {
        JFTPClient client = null;
        try {
            client = createClient(protocol, credentials);
            boolean success = client.uploadFile(localFile, remoteFile);
            
            if (success) {
                System.out.println("File transferred successfully via " + protocol);
            }
        } catch (FTPException e) {
            System.err.println("Transfer failed: " + e.getMessage());
        } finally {
            if (client != null) {
                try {
                    client.disconnect();
                } catch (FTPException e) {
                    // Log disconnect error
                }
            }
        }
    }
}
```

### Custom File Filter Implementations

```java
// Filter by file extension
public class ExtensionFilter implements FileFilter<RemoteFile> {
    private final String[] extensions;
    
    public ExtensionFilter(String... extensions) {
        this.extensions = extensions;
    }
    
    @Override
    public boolean accept(RemoteFile file) {
        if (!file.isFile()) return false;
        String name = file.getName().toLowerCase();
        for (String ext : extensions) {
            if (name.endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

// Usage
FileFilter<RemoteFile> documentFilter = new ExtensionFilter(".pdf", ".doc", ".docx");
List<RemoteFile> documents = ftpClient.listFiles("/documents", documentFilter);

// Filter by size range
public class SizeRangeFilter implements FileFilter<RemoteFile> {
    private final long minSize;
    private final long maxSize;
    
    public SizeRangeFilter(long minSize, long maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    @Override
    public boolean accept(RemoteFile file) {
        return file.isFile() && 
               file.getSize() >= minSize && 
               file.getSize() <= maxSize;
    }
}

// Usage: files between 1MB and 10MB
FileFilter<RemoteFile> sizeFilter = new SizeRangeFilter(1024 * 1024, 10 * 1024 * 1024);
List<RemoteFile> mediumFiles = ftpClient.listFiles("/uploads", sizeFilter);

// Filter by date range
public class DateRangeFilter implements FileFilter<RemoteFile> {
    private final long startTime;
    private final long endTime;
    
    public DateRangeFilter(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    @Override
    public boolean accept(RemoteFile file) {
        if (!file.isFile() || file.getTimestamp() == null) return false;
        long fileTime = file.getTimestamp().getTimeInMillis();
        return fileTime >= startTime && fileTime <= endTime;
    }
}
```

## Error Handling

### FTPException Handling

All FTP operations throw `FTPException` for error conditions:

```java
try {
    JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
    ftpClient.connect(credentials);
    
    boolean success = ftpClient.uploadFile(localPath, remotePath);
    
    ftpClient.disconnect();
    
} catch (FTPException e) {
    System.err.println("FTP operation failed: " + e.getMessage());
    e.printStackTrace();
    
    // Handle specific error scenarios
    if (e.getMessage().contains("login")) {
        System.err.println("Authentication failed - check credentials");
    } else if (e.getMessage().contains("timeout")) {
        System.err.println("Connection timed out - check network");
    }
}
```

### Safe Disconnect Pattern

```java
JFTPClient ftpClient = null;
try {
    ftpClient = new JFTPClient(FTPType.FTP);
    ftpClient.connect(credentials);
    
    // Perform operations
    List<RemoteFile> files = ftpClient.listFiles("/path", null);
    
} catch (FTPException e) {
    System.err.println("Error: " + e.getMessage());
} finally {
    if (ftpClient != null) {
        try {
            ftpClient.disconnect();
        } catch (FTPException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
```

### Retry Logic

```java
public boolean uploadWithRetry(JFTPClient ftpClient, String local, String remote, int maxRetries) {
    int attempt = 0;
    while (attempt < maxRetries) {
        try {
            boolean success = ftpClient.uploadFile(local, remote);
            if (success) {
                System.out.println("Upload succeeded on attempt " + (attempt + 1));
                return true;
            }
        } catch (FTPException e) {
            attempt++;
            System.err.println("Upload attempt " + attempt + " failed: " + e.getMessage());
            
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(1000 * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    return false;
}
```

## RemoteFile Object

The `RemoteFile` class represents a file or directory on the remote server:

```java
RemoteFile file = ...;

// Basic properties
String name = file.getName();           // File name
boolean isFile = file.isFile();         // Is it a file?
boolean isDir = file.isDirectory();     // Is it a directory?
long size = file.getSize();             // File size in bytes
String path = file.getPath();           // Full path

// Timestamp
Calendar timestamp = file.getTimestamp();
if (timestamp != null) {
    Date modifiedDate = timestamp.getTime();
    System.out.println("Last modified: " + modifiedDate);
}
```

## Best Practices

### 1. Always Disconnect

```java
JFTPClient ftpClient = new JFTPClient(FTPType.FTP);
try {
    ftpClient.connect(credentials);
    // Operations
} finally {
    try {
        ftpClient.disconnect();
    } catch (FTPException e) {
        // Log error
    }
}
```

### 2. Use Try-With-Resources Pattern (Custom Wrapper)

```java
public class AutoCloseFTPClient implements AutoCloseable {
    private final JFTPClient ftpClient;
    
    public AutoCloseFTPClient(FTPType type, Credentials credentials) throws FTPException {
        this.ftpClient = new JFTPClient(type);
        this.ftpClient.connect(credentials);
    }
    
    public JFTPClient getClient() {
        return ftpClient;
    }
    
    @Override
    public void close() {
        try {
            ftpClient.disconnect();
        } catch (FTPException e) {
            // Log error
        }
    }
}

// Usage
try (AutoCloseFTPClient autoClient = new AutoCloseFTPClient(FTPType.FTP, credentials)) {
    JFTPClient ftpClient = autoClient.getClient();
    // Perform operations
} // Automatically disconnects
```

### 3. Configure Appropriate Timeouts

```java
Credentials credentials = Credentials.builder()
    .host("ftp.example.com")
    .port(21)
    .username("user")
    .password("password")
    .connectTimeout(30000)   // 30 seconds for connection
    .socketTimeout(300000)    // 5 minutes for large file transfers
    .build();
```

### 4. Validate Paths

```java
public boolean uploadFileWithValidation(JFTPClient ftpClient, String local, String remote) {
    File localFile = new File(local);
    
    if (!localFile.exists()) {
        System.err.println("Local file does not exist: " + local);
        return false;
    }
    
    if (!localFile.canRead()) {
        System.err.println("Cannot read local file: " + local);
        return false;
    }
    
    try {
        return ftpClient.uploadFile(local, remote);
    } catch (FTPException e) {
        System.err.println("Upload failed: " + e.getMessage());
        return false;
    }
}
```

## API Reference

### JFTPClient

Main client class for FTP operations.

**Constructor:**
- `JFTPClient(FTPType ftpType)` - Create client for specified protocol type

**Methods:**
- `void connect(Credentials credentials)` - Connect to FTP server
- `void disconnect()` - Disconnect from FTP server
- `List<RemoteFile> listFiles(String directoryPath, FileFilter<RemoteFile> fileFilter)` - List files with optional filter
- `boolean uploadFile(String localFilePath, String remoteFilePath)` - Upload file to server
- `boolean downloadFile(String remoteFilePath, String localFilePath)` - Download file from server
- `boolean deleteFile(String remoteFilePath)` - Delete file from server

### Credentials

Configuration for FTP connection.

**Builder Methods:**
- `host(String host)` - Set hostname
- `port(int port)` - Set port number
- `username(String username)` - Set username
- `password(String password)` - Set password
- `connectTimeout(int timeout)` - Set connection timeout in milliseconds (default: 15000)
- `socketTimeout(int timeout)` - Set socket timeout in milliseconds (default: 60000)
- `isImplicit(boolean implicit)` - Set implicit SSL mode for FTPS (default: false)

### FTPType

Enum for FTP protocol types.

**Values:**
- `FTP` - Standard FTP protocol
- `SFTP` - SSH File Transfer Protocol
- `FTPS` - FTP over SSL/TLS

### RemoteFile

Represents a remote file or directory.

**Properties:**
- `String name` - File/directory name
- `boolean isFile` - Whether it's a file
- `boolean isDirectory` - Whether it's a directory
- `long size` - File size in bytes
- `Calendar timestamp` - Last modification time
- `String path` - Full path on server

### FileFilter<RemoteFile>

Interface for filtering files.

**Method:**
- `boolean accept(RemoteFile file)` - Returns true if file should be included

## Requirements

- Java 11 or higher
- Apache Commons Net 3.12.0
- JSch 2.27.5 (for SFTP)
- SLF4J 2.0.16

## Dependencies

This module depends on:
- `com.javaquery:util` - Utility classes

## License

This project is part of the JLite library suite.

## Contributing

Contributions are welcome! Please ensure all tests pass before submitting pull requests.

## Author

**javaquery**

## Version

Current version: **1.0.1**

---

For more information and updates, visit the [JLite GitHub repository](https://github.com/javaquery/JLite).

