# Spring AWS S3 Module

A lightweight and powerful Spring Boot module for interacting with AWS S3, featuring presigned URLs, async operations, and automatic credential management with zero-configuration setup.

## Features

- 🚀 **Spring Boot Auto-Configuration** - Zero setup, works out of the box
- 📤 **Upload Operations** - Direct uploads with metadata and tags support
- 📥 **Download Operations** - Download to temp files or generate presigned URLs
- 🔗 **Presigned URLs** - Generate secure temporary URLs for uploads and downloads
- 🗑️ **Batch Operations** - Delete single or multiple objects efficiently
- 📋 **Copy & Move** - Copy and move objects within or across buckets
- ⚡ **Async Support** - Non-blocking operations with CompletableFuture
- 🔐 **Flexible Authentication** - Static credentials or AWS default credential chain
- 🌍 **Multi-Region Support** - Configure default region or specify per operation
- ⏱️ **Configurable Expiration** - Control presigned URL duration
- 🏷️ **Metadata & Tags** - Full support for object metadata and tags
- 📝 **Automatic Content-Type Detection** - Smart content type resolution
- 🛡️ **Error Handling** - Comprehensive error logging and handling

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:spring-aws-s3:1.0.0'
    implementation 'software.amazon.awssdk:s3:2.41.10'
}
```

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.javaquery</groupId>
        <artifactId>spring-aws-s3</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.41.10</version>
    </dependency>
</dependencies>
```

> **✨ Auto-Configuration**: This module uses Spring Boot auto-configuration. Simply add the dependency and the `S3Service` bean will be automatically available for injection - no manual configuration or component scanning required!

## Configuration

### application.yml

#### With Static Credentials

```yaml
aws:
  accessKeyId: ${AWS_ACCESS_KEY_ID}
  secretAccessKey: ${AWS_SECRET_ACCESS_KEY}
  providerName: MyProvider        # Optional
  accountId: 123456789012         # Optional

aws:
  s3:
    region: us-east-1              # Default region for S3 operations (default: us-east-1)
  signature:
    duration: 5                    # Presigned URL duration in minutes (default: 5)
```

#### Using AWS Default Credential Chain

```yaml
# No credentials needed - automatically uses AWS default credential chain
aws:
  s3:
    region: us-west-2
  signature:
    duration: 10
```

### application.properties

```properties
# Static credentials (optional)
aws.accessKeyId=${AWS_ACCESS_KEY_ID}
aws.secretAccessKey=${AWS_SECRET_ACCESS_KEY}
aws.providerName=MyProvider
aws.accountId=123456789012

# S3 configuration
aws.s3.region=us-east-1
aws.signature.duration=5
```

### Credential Resolution

If `accessKeyId` and `secretAccessKey` are not configured, the module automatically uses AWS default credential chain:
1. Environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)
2. AWS credentials file (`~/.aws/credentials`)
3. IAM role (when running on EC2, ECS, Lambda, etc.)

## Quick Start

### Basic Usage

```java
import com.javaquery.spring.aws.S3Service;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class FileStorageService {
    
    private final S3Service s3Service;
    
    public FileStorageService(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    public String uploadFile(File file) {
        return s3Service.uploadObject(
            S3Service.S3UploadObject.builder()
                .bucketName("my-bucket")
                .destination("uploads/" + file.getName())
                .source(file)
                .build()
        );
    }
}
```

## Examples

### 1. Upload Object with Metadata and Tags

```java
Map<String, String> metadata = new HashMap<>();
metadata.put("Content-Type", "image/jpeg");
metadata.put("author", "John Doe");

Map<String, String> tags = new HashMap<>();
tags.put("environment", "production");
tags.put("department", "marketing");

String downloadUrl = s3Service.uploadObject(
    S3Service.S3UploadObject.builder()
        .region(Region.US_EAST_1)
        .bucketName("my-bucket")
        .destination("images/photo.jpg")
        .source(new File("/path/to/photo.jpg"))
        .metadata(metadata)
        .tags(tags)
        .build()
);

System.out.println("Download URL: " + downloadUrl);
```

### 2. Generate Presigned Upload URL

```java
// Using default region
String uploadUrl = s3Service.getPutPresignedUrl("my-bucket", "uploads/document.pdf");

// With specific region
String uploadUrl = s3Service.getPutPresignedUrl(
    Region.EU_WEST_1, 
    "my-bucket", 
    "uploads/document.pdf"
);

// Client can now upload directly to this URL
System.out.println("Upload to: " + uploadUrl);
```

### 3. Generate Presigned Download URL

```java
// Using default region
String downloadUrl = s3Service.getGetPresignedUrl("my-bucket", "documents/report.pdf");

// With specific region
String downloadUrl = s3Service.getGetPresignedUrl(
    Region.AP_SOUTHEAST_1,
    "my-bucket",
    "documents/report.pdf"
);

// Share this URL with users for temporary access
System.out.println("Download from: " + downloadUrl);
```

### 4. Download Object

```java
// Download to temporary directory using default region
String filePath = s3Service.downloadObject("my-bucket", "documents/file.pdf");
System.out.println("Downloaded to: " + filePath);

// With specific region
String filePath = s3Service.downloadObject(
    Region.US_WEST_2,
    "my-bucket",
    "documents/file.pdf"
);

File downloadedFile = new File(filePath);
// Process the file...
```

### 5. Delete Single Object

```java
// Delete using default region
s3Service.deleteObject("my-bucket", "old-files/temp.txt");

System.out.println("Object deleted successfully");
```

### 6. Delete Multiple Objects

```java
List<String> objectKeys = Arrays.asList(
    "temp/file1.txt",
    "temp/file2.txt",
    "temp/file3.txt"
);

s3Service.deleteObjects(
    Region.US_EAST_1,
    "my-bucket",
    objectKeys
);

System.out.println("All objects deleted");
```

### 7. Copy Object

```java
// Copy within same bucket
CompletableFuture<String> result = s3Service.copyObjectAsync(
    Region.US_EAST_1,
    "my-bucket",
    "source/original.pdf",
    "backup/original-copy.pdf"
);

result.thenAccept(copyResult -> {
    System.out.println("Copy completed: " + copyResult);
});

// Copy to different bucket
CompletableFuture<String> result = s3Service.copyObjectAsync(
    Region.US_EAST_1,
    "source-bucket",
    "documents/file.pdf",
    "destination-bucket",
    "archived/file.pdf"
);
```

### 8. Move Object

```java
// Move within same bucket (copy + delete source)
s3Service.moveObject(
    Region.EU_CENTRAL_1,
    "my-bucket",
    "temp/file.txt",
    "my-bucket",
    "archive/file.txt"
);

// Move to different bucket
s3Service.moveObject(
    Region.US_EAST_1,
    "source-bucket",
    "uploads/file.pdf",
    "archive-bucket",
    "2026/01/file.pdf"
);
```

### 9. Upload with Auto Content-Type Detection

```java
// Content-Type is automatically detected from file extension
String downloadUrl = s3Service.uploadObject(
    S3Service.S3UploadObject.builder()
        .bucketName("my-bucket")
        .destination("images/photo.png")
        .source(new File("photo.png"))
        .build()
);
// Content-Type will be set to "image/png"
```

### 10. Async Upload Processing

```java
@Service
public class AsyncUploadService {
    
    private final S3Service s3Service;
    
    @Async
    public CompletableFuture<String> uploadFileAsync(File file, String bucketName) {
        return CompletableFuture.supplyAsync(() -> {
            return s3Service.uploadObject(
                S3Service.S3UploadObject.builder()
                    .bucketName(bucketName)
                    .destination("uploads/" + file.getName())
                    .source(file)
                    .build()
            );
        });
    }
}
```

## Advanced Usage

### Multi-Region Upload Strategy

```java
@Service
public class GlobalStorageService {
    
    private final S3Service s3Service;
    
    public String uploadToNearestRegion(File file, String userLocation) {
        Region region = determineRegion(userLocation);
        
        return s3Service.uploadObject(
            S3Service.S3UploadObject.builder()
                .region(region)
                .bucketName("global-bucket-" + region.id())
                .destination("uploads/" + file.getName())
                .source(file)
                .build()
        );
    }
    
    private Region determineRegion(String location) {
        // Logic to determine optimal region based on user location
        return switch (location) {
            case "EU" -> Region.EU_WEST_1;
            case "ASIA" -> Region.AP_SOUTHEAST_1;
            default -> Region.US_EAST_1;
        };
    }
}
```

### Batch Processing with Error Handling

```java
@Service
public class BatchProcessingService {
    
    private final S3Service s3Service;
    
    public void cleanupOldFiles(List<String> oldFileKeys) {
        try {
            s3Service.deleteObjects(
                Region.US_EAST_1,
                "my-bucket",
                oldFileKeys
            );
            log.info("Successfully deleted {} files", oldFileKeys.size());
        } catch (Exception e) {
            log.error("Failed to delete files", e);
            // Implement retry logic or error notification
        }
    }
}
```

### Dynamic Bucket Selection

```java
@Service
public class TenantStorageService {
    
    private final S3Service s3Service;
    
    public String uploadForTenant(String tenantId, File file) {
        String bucketName = "tenant-" + tenantId;
        String objectKey = generateObjectKey(tenantId, file);
        
        return s3Service.uploadObject(
            S3Service.S3UploadObject.builder()
                .bucketName(bucketName)
                .destination(objectKey)
                .source(file)
                .metadata(Map.of(
                    "tenant-id", tenantId,
                    "upload-date", LocalDateTime.now().toString()
                ))
                .tags(Map.of(
                    "tenant", tenantId,
                    "category", "user-uploads"
                ))
                .build()
        );
    }
}
```

## API Reference

### S3Service Methods

#### Upload Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `uploadObject` | `S3UploadObject` | `String` | Upload object and return presigned download URL |
| `getPutPresignedUrl` | `bucketName, objectKey` | `String` | Generate presigned upload URL (default region) |
| `getPutPresignedUrl` | `region, bucketName, objectKey` | `String` | Generate presigned upload URL |

#### Download Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `downloadObject` | `bucketName, objectKey` | `String` | Download to temp file (default region) |
| `downloadObject` | `region, bucketName, objectKey` | `String` | Download to temp file |
| `getGetPresignedUrl` | `bucketName, objectKey` | `String` | Generate presigned download URL (default region) |
| `getGetPresignedUrl` | `region, bucketName, objectKey` | `String` | Generate presigned download URL |

#### Delete Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `deleteObject` | `bucketName, objectKey` | `void` | Delete single object (default region) |
| `deleteObjects` | `region, bucketName, objectKeys` | `void` | Delete multiple objects |

#### Copy/Move Operations

| Method | Parameters | Return | Description |
|--------|-----------|--------|-------------|
| `copyObjectAsync` | `region, sourceBucket, sourceKey, destBucket, destKey` | `CompletableFuture<String>` | Copy object across buckets |
| `copyObjectAsync` | `region, bucketName, sourceKey, destKey` | `CompletableFuture<String>` | Copy within bucket |
| `moveObject` | `region, sourceBucket, sourceKey, destBucket, destKey` | `void` | Move object (copy + delete) |

### S3UploadObject Builder

| Property | Type | Required | Default | Description |
|----------|------|----------|---------|-------------|
| `region` | `Region` | No | `US_EAST_1` | AWS region |
| `bucketName` | `String` | Yes | - | S3 bucket name |
| `destination` | `String` | Yes | - | Object key/path |
| `source` | `File` | Yes | - | File to upload |
| `tags` | `Map<String, String>` | No | - | Object tags |
| `metadata` | `Map<String, String>` | No | - | Object metadata |

### Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `aws.accessKeyId` | `String` | - | AWS access key (optional with default chain) |
| `aws.secretAccessKey` | `String` | - | AWS secret key (optional with default chain) |
| `aws.providerName` | `String` | - | Credential provider name |
| `aws.accountId` | `String` | - | AWS account ID |
| `aws.s3.region` | `String` | `us-east-1` | Default S3 region |
| `aws.signature.duration` | `int` | `5` | Presigned URL duration (minutes) |

## Best Practices

### 1. Use Presigned URLs for Client Uploads

```java
// Generate presigned URL and return to client
String uploadUrl = s3Service.getPutPresignedUrl("my-bucket", "uploads/file.pdf");

// Client uploads directly to S3 (no server bandwidth used)
// POST to uploadUrl with file content
```

### 2. Implement Error Handling

```java
try {
    s3Service.deleteObjects(region, bucket, keys);
} catch (Exception e) {
    log.error("Failed to delete objects from S3", e);
    // Implement retry logic or notification
}
```

### 3. Use Async Operations for Large Files

```java
CompletableFuture<String> copyFuture = s3Service.copyObjectAsync(
    region, sourceBucket, sourceKey, destBucket, destKey
);

copyFuture.thenAccept(result -> {
    log.info("Copy completed: {}", result);
}).exceptionally(ex -> {
    log.error("Copy failed", ex);
    return null;
});
```

### 4. Set Appropriate Metadata

```java
Map<String, String> metadata = new HashMap<>();
metadata.put("Content-Type", "application/pdf");
metadata.put("Content-Disposition", "attachment; filename=\"report.pdf\"");
metadata.put("Cache-Control", "max-age=3600");
```

### 5. Use Tags for Organization

```java
Map<String, String> tags = new HashMap<>();
tags.put("environment", "production");
tags.put("cost-center", "engineering");
tags.put("retention", "7-years");
```

## Security Considerations

### 1. IAM Permissions

Ensure your AWS credentials or IAM role has the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:PutObjectTagging",
        "s3:GetObjectTagging"
      ],
      "Resource": "arn:aws:s3:::your-bucket/*"
    }
  ]
}
```

### 2. Presigned URL Security

- Keep duration short (5-10 minutes for uploads, 1 hour for downloads)
- Generate URLs on-demand
- Don't expose URLs in public logs
- Consider implementing rate limiting

### 3. Credential Management

- Use AWS Secrets Manager or Parameter Store for credentials
- Use IAM roles when running on AWS infrastructure
- Rotate credentials regularly
- Never commit credentials to version control

## Troubleshooting

### Issue: "Access Denied" Error

**Solution**: Check IAM permissions and ensure credentials are correctly configured.

```yaml
aws:
  accessKeyId: ${AWS_ACCESS_KEY_ID}
  secretAccessKey: ${AWS_SECRET_ACCESS_KEY}
```

### Issue: "Bucket Not Found"

**Solution**: Verify bucket name and region match your configuration.

```java
// Ensure region matches bucket location
s3Service.uploadObject(
    S3Service.S3UploadObject.builder()
        .region(Region.EU_WEST_1)  // Must match bucket region
        .bucketName("my-eu-bucket")
        .build()
);
```

### Issue: Presigned URL Expires Immediately

**Solution**: Increase signature duration in configuration.

```yaml
aws:
  signature:
    duration: 15  # Increase to 15 minutes
```

### Issue: Content-Type Not Set Correctly

**Solution**: Explicitly set Content-Type in metadata.

```java
Map<String, String> metadata = new HashMap<>();
metadata.put("Content-Type", "application/pdf");
```

## Performance Tips

1. **Use async operations** for large file operations
2. **Batch delete operations** instead of deleting one by one
3. **Use presigned URLs** to offload upload/download traffic from your server
4. **Choose regions wisely** - store data close to users
5. **Enable S3 Transfer Acceleration** for global uploads
6. **Use appropriate part sizes** for multipart uploads (future feature)

## Testing

### Unit Testing with Mocking

```java
@SpringBootTest
class FileStorageServiceTest {
    
    @MockBean
    private S3Service s3Service;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Test
    void testUploadFile() {
        when(s3Service.uploadObject(any()))
            .thenReturn("https://s3.amazonaws.com/bucket/file.pdf");
        
        String url = fileStorageService.uploadFile(new File("test.pdf"));
        
        assertNotNull(url);
        verify(s3Service, times(1)).uploadObject(any());
    }
}
```

### Integration Testing

For integration tests, consider using [LocalStack](https://github.com/localstack/localstack) or [S3Mock](https://github.com/adobe/S3Mock).

## Migration Guide

### From Manual Configuration

If you were using manual component scanning:

**Before:**
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.myapp", "com.javaquery.spring.aws"})
public class MyApplication {
    // ...
}
```

**After:**
```java
@SpringBootApplication
public class MyApplication {
    // Auto-configuration handles everything!
}
```

## Version Compatibility

| Module Version | Spring Boot | Java | AWS SDK S3 | Status |
|----------------|-------------|------|------------|--------|
| 1.0.0 | 3.0.x - 3.5.x | 17+ | 2.41.10 | ✅ Stable |
| 1.0.0 | 2.7.x | 11+ | 2.41.10 | ✅ Stable |

## Dependencies

- `spring-boot-autoconfigure` - Auto-configuration support
- `software.amazon.awssdk:s3` - AWS SDK for S3
- `javaquery:util` - Utility functions

## Contributing

Contributions are welcome! Please ensure:
- Code follows existing style conventions
- All tests pass
- New features include tests and documentation

## License

This project is licensed under the same license as the JLite project.

## Support

For issues, questions, or contributions, please visit the [JLite GitHub repository](https://github.com/javaquery/JLite).

## Related Modules

- [spring-email](../spring-email) - Email sending with auto-configuration
- [spring-aws-cognito](../spring-aws-cognito) - AWS Cognito integration
- [httpclient](../../core/httpclient) - HTTP client utilities

---

**Built with ❤️ by the JLite Team**
