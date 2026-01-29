# Changelog

All notable changes to the module:spring-aws-s3 module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-01-29

### Added

#### Core Features
- **S3Service** - Comprehensive service for AWS S3 operations
  - Upload, download, delete, copy, and move operations
  - Presigned URL generation for secure temporary access
  - Async operations with CompletableFuture support
  - Multi-region support for global deployments
- **Spring Boot Auto-Configuration**
  - Automatic bean registration via `AwsS3AutoConfiguration`
  - Zero configuration setup - works out of the box
  - No manual component scanning required
  - Conditional activation based on AWS SDK presence
  - Respects custom `S3Service` implementations
- **Flexible Credential Management**
  - Static credentials via configuration properties
  - AWS default credential chain support
  - Automatic selection between static and default providers
  - Environment variable support
  - IAM role support for AWS infrastructure
- **Metadata and Tags Support**
  - Full object metadata management
  - Object tagging capabilities
  - Custom Content-Type configuration
  - Automatic Content-Type detection from file extensions

#### Upload Operations
- **Direct Upload** - `uploadObject(S3UploadObject)`
  - Upload files with metadata and tags
  - Returns presigned download URL
  - Automatic content type detection
  - Support for custom metadata
  - Async upload with CompletableFuture
- **Presigned Upload URLs** - `getPutPresignedUrl()`
  - Generate temporary upload URLs
  - Configurable expiration duration
  - Support for default or specific regions
  - Client-side upload capability (no server bandwidth)
- **Upload Builder Pattern** - `S3UploadObject.builder()`
  - Fluent API for upload configuration
  - Optional metadata and tags
  - Region specification
  - Source file and destination configuration

#### Download Operations
- **Direct Download** - `downloadObject()`
  - Download objects to temporary files
  - Support for default or specific regions
  - Async download operations
  - Automatic file path management
- **Presigned Download URLs** - `getGetPresignedUrl()`
  - Generate temporary download URLs
  - Configurable expiration duration
  - Secure temporary access without credentials
  - Share-friendly URLs for external access

#### Delete Operations
- **Single Object Delete** - `deleteObject()`
  - Delete individual objects
  - Uses default region configuration
  - Error logging for failed operations
- **Batch Delete** - `deleteObjects()`
  - Delete multiple objects in one operation
  - Efficient batch processing
  - Region-specific operations
  - Comprehensive error reporting
  - Individual error tracking per object

#### Copy & Move Operations
- **Async Copy** - `copyObjectAsync()`
  - Copy objects within same bucket
  - Copy across different buckets
  - Cross-region copy support
  - Returns CompletableFuture for async handling
  - Error handling with logging
- **Move Operation** - `moveObject()`
  - Move objects by copying and deleting source
  - Supports cross-bucket moves
  - Cross-region move capability
  - Atomic operation (copy then delete)
  - Error handling for partial failures

#### Configuration Management
- **AwsProperties** - Configuration properties class
  - `aws.accessKeyId` - AWS access key ID
  - `aws.secretAccessKey` - AWS secret access key
  - `aws.providerName` - Optional provider name
  - `aws.accountId` - Optional AWS account ID
- **S3-Specific Configuration**
  - `aws.s3.region` - Default S3 region (default: us-east-1)
  - `aws.signature.duration` - Presigned URL duration in minutes (default: 5)
- **AmazonWebServices Configuration**
  - Creates `AwsCredentialsProvider` bean
  - Automatic provider selection logic
  - Support for `StaticCredentialsProvider`
  - Support for `DefaultCredentialsProvider`

#### Spring Boot Integration
- **Auto-Configuration Class** - `AwsS3AutoConfiguration`
  - `@AutoConfiguration` for automatic loading
  - `@ConditionalOnClass` for smart activation
  - `@EnableConfigurationProperties` for property binding
  - `@Import(AmazonWebServices.class)` for credential provider
  - `@ConditionalOnMissingBean` to respect custom implementations
- **META-INF Registration**
  - `spring.factories` file for Spring Boot 2.x
  - Automatic discovery by Spring Boot
  - No manual registration required
- **Dependency Injection**
  - Constructor-based injection
  - Automatic `AwsCredentialsProvider` wiring
  - Clean dependency management

#### Testing Support
- **Comprehensive Test Coverage**
  - `AwsS3AutoConfigurationTest` - Auto-configuration verification
  - Tests for bean creation
  - Tests for conditional activation
  - Tests for custom bean respect
  - Tests for default credential chain
- **ApplicationContextRunner Testing**
  - Proper auto-configuration testing approach
  - Context-based verification
  - Property injection testing
  - Bean presence validation

#### Documentation
- **Comprehensive README.md**
  - Feature overview with emojis
  - Installation instructions (Gradle & Maven)
  - Configuration examples (YAML & Properties)
  - Credential resolution strategies
  - 10+ usage examples
  - API reference tables
  - Best practices guide
  - Security considerations
  - Troubleshooting section
  - Performance tips
  - Testing examples
  - Migration guide
- **CHANGELOG.md** (this file)
  - Version history
  - Feature documentation
  - Breaking changes tracking
  - Future roadmap

#### Error Handling & Logging
- **SLF4J Integration**
  - Comprehensive error logging via `@Slf4j`
  - Detailed error messages
  - Exception stack traces
  - Operation-specific logging
- **Error Recovery**
  - Individual error tracking in batch deletes
  - Error code and message reporting
  - Graceful failure handling
  - CompletableFuture exception handling

#### Performance Features
- **Async Operations**
  - Non-blocking uploads and downloads
  - CompletableFuture-based API
  - Parallel operation support
  - Efficient resource utilization
- **Batch Processing**
  - Efficient multi-object delete
  - Single API call for multiple objects
  - Reduced network overhead
- **Resource Management**
  - Automatic S3AsyncClient lifecycle
  - Try-with-resources for proper cleanup
  - S3Presigner resource management

#### Content Type Handling
- **Automatic Detection**
  - File extension-based detection
  - Fallback to metadata
  - Uses JLite util library
- **Manual Override**
  - Custom Content-Type in metadata
  - Full control over content types
  - Standard MIME type support

#### Region Support
- **Default Region Configuration**
  - Configurable default via properties
  - Fallback to us-east-1
  - Used when region not specified
- **Per-Operation Region**
  - Specify region for each operation
  - Multi-region deployment support
  - Cross-region operations
  - Global application support

### Technical Details

#### Architecture
- **Service Layer Pattern**
  - Clean separation of concerns
  - Stateless service design
  - Dependency injection friendly
- **Builder Pattern**
  - `S3UploadObject` with Lombok `@Builder`
  - Fluent API for configuration
  - Optional parameters support
  - Type-safe construction
- **Async/Await Pattern**
  - CompletableFuture for async operations
  - Non-blocking I/O
  - Reactive-style programming support

#### Dependencies
- **Core Dependencies**
  - `software.amazon.awssdk:s3:2.41.10` (compileOnly)
  - `spring-boot-autoconfigure` (implementation)
  - `javaquery:util` (api)
- **Build Tools**
  - `spring-boot-autoconfigure-processor` (annotationProcessor)
  - Configuration metadata generation
- **Test Dependencies**
  - `software.amazon.awssdk:s3:2.41.10` (testImplementation)
  - `spring-boot-starter-test` (testImplementation)

#### AWS SDK Integration
- **S3AsyncClient**
  - Async operations via AWS SDK
  - Region-specific client creation
  - Credential provider injection
  - Automatic resource cleanup
- **S3Presigner**
  - Presigned URL generation
  - Configurable expiration
  - Region-aware presigning
  - Automatic credential signing

### Configuration Flexibility
- **Multiple Configuration Methods**
  - YAML configuration files
  - Properties files
  - Environment variables
  - Programmatic configuration
- **Profile-Based Configuration**
  - Different credentials per environment
  - Region selection per profile
  - Easy environment switching

### Security Features
- **Secure Credential Handling**
  - No hardcoded credentials
  - External configuration support
  - AWS credential chain integration
- **Presigned URL Security**
  - Configurable expiration
  - Temporary access control
  - No credential exposure
- **IAM Integration**
  - Automatic IAM role support
  - Environment-based credentials
  - AWS infrastructure integration

### Examples Provided
1. Upload object with metadata and tags
2. Generate presigned upload URL
3. Generate presigned download URL
4. Download object to file
5. Delete single object
6. Delete multiple objects (batch)
7. Copy object within/across buckets
8. Move object (copy + delete)
9. Upload with auto content-type detection
10. Async upload processing
11. Multi-region upload strategy
12. Batch processing with error handling
13. Dynamic bucket selection
14. Tenant-based storage

### Breaking Changes
- None (initial release)

### Migration Notes
- No migration needed (initial release)
- If upgrading from pre-1.0.0 versions that required manual component scanning:
  - Remove `@ComponentScan(basePackages = "com.javaquery.spring.aws")` from application
  - Remove manual `@Bean` definitions for `S3Service`
  - Remove manual `@Configuration` for AWS properties
  - The module now auto-configures automatically

### Known Limitations
- Auto-configuration designed for Spring Boot 2.7.x
  - For Spring Boot 3.x, `spring.factories` location needs updating to `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Requires `software.amazon.awssdk:s3` on classpath
- Presigned URLs have maximum duration limitation (7 days by AWS)
- Multipart upload not yet implemented (planned for future release)
- S3 Transfer Acceleration support not included (planned)

### Deprecations
- None (initial release)

### Removed
- Manual `@Component` annotation from `S3Service` (replaced with auto-configuration)
- Redundant `@Configuration` from `AwsProperties` (now pure properties class)

### Fixed
- N/A (initial release)

### Security
- No known vulnerabilities
- Uses latest AWS SDK S3 (2.41.10)
- Secure credential management practices

### Performance
- Async operations for non-blocking I/O
- Batch delete for efficient multi-object operations
- Resource pooling via AWS SDK
- Optimized client lifecycle management

### Contributors
- Vicky Thakor (@javaquery)

### Links
- [GitHub Repository](https://github.com/javaquery/JLite)
- [Documentation](README.md)
- [AWS SDK Documentation](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)

---

## Version History

### Release Schedule
- **1.0.0** - January 29, 2026 - Initial stable release with auto-configuration

### Compatibility Matrix

| Module Version | Spring Boot | Java | AWS SDK S3 | Status |
|----------------|-------------|------|------------|--------|
| 1.0.0 | 2.7.x | 11+ | 2.41.10 | ✅ Stable |

### Future Roadmap

#### Version 1.1.0 (Planned)
- **Multipart Upload Support**
  - Large file uploads (>5GB)
  - Automatic part size calculation
  - Resume capability
  - Progress tracking
- **S3 Transfer Acceleration**
  - Faster global uploads
  - Automatic endpoint selection
  - Configuration support
- **Bucket Operations**
  - Create bucket
  - Delete bucket
  - List buckets
  - Bucket policy management
- **Object Listing**
  - List objects in bucket
  - Pagination support
  - Prefix filtering
  - Delimiter support

#### Version 1.2.0 (Planned)
- **Spring Boot 3.x Support**
  - Update auto-configuration registration
  - Jakarta EE namespace migration
  - Native compilation support
- **Enhanced Metadata**
  - Custom metadata retrieval
  - Metadata update operations
  - System metadata access
- **Lifecycle Management**
  - Lifecycle policy configuration
  - Object expiration rules
  - Transition policies
- **Versioning Support**
  - Enable/disable versioning
  - Version listing
  - Specific version operations

#### Version 2.0.0 (Future)
- **CloudFront Integration**
  - CDN URL generation
  - Invalidation support
  - Edge location optimization
- **S3 Select Support**
  - Query objects with SQL
  - Filtered data retrieval
  - Reduced data transfer
- **Event Notifications**
  - S3 event integration
  - SNS/SQS notification
  - Lambda trigger support
- **Access Control**
  - ACL management
  - Bucket policy operations
  - Public access blocking
- **Encryption Management**
  - Server-side encryption
  - KMS integration
  - Client-side encryption
- **Metrics and Monitoring**
  - Operation metrics
  - Performance tracking
  - Cost estimation

### Upgrade Guides

#### From Pre-Release to 1.0.0
If you were using development versions:

1. **Update Dependency**
   ```gradle
   implementation 'com.javaquery:spring-aws-s3:1.0.0'
   ```

2. **Remove Manual Configuration**
   - Remove `@ComponentScan` for `com.javaquery.spring.aws`
   - Remove manual `@Bean` definitions
   - Auto-configuration handles everything

3. **Update Properties**
   - Ensure `aws.s3.region` is set (default: us-east-1)
   - Configure `aws.signature.duration` if needed (default: 5 minutes)

4. **Test Injection**
   ```java
   @Autowired
   private S3Service s3Service; // Now auto-injected
   ```

### Support and Feedback
- **Issues**: [GitHub Issues](https://github.com/javaquery/JLite/issues)
- **Discussions**: [GitHub Discussions](https://github.com/javaquery/JLite/discussions)
- **Documentation**: [README.md](README.md)

---

*For detailed usage examples and API documentation, see [README.md](README.md)*
