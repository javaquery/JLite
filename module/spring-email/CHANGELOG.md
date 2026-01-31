# Changelog

All notable changes to the module:spring-email module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-01-31

### Changed

#### Breaking Change - Spring Boot 3.x Minimum Requirement
- **Spring Boot 3.x+ Only** - This library now requires Spring Boot 3.0.x or above
  - ⚠️ **Breaking Change**: Dropped Spring Boot 2.x support
  - Migrated from `javax.*` to `jakarta.*` namespace
  - Requires Java 17 or higher
  - Compatible with Spring Boot 3.0.x, 3.1.x, 3.2.x, 3.3.x, 3.4.x, and 3.5.x
  - Tested and verified with Spring Boot 3.5.10

#### Jakarta EE Migration
- **Jakarta Namespace** - Fully migrated to Jakarta EE standards
  - Uses `jakarta.mail.*` instead of `javax.mail.*`
  - Compatible with Jakarta Mail API
  - No backward compatibility with javax namespace
  - Ensures compliance with modern Java EE standards

### Technical Details

#### Dependency Requirements
- **Minimum Versions**
  - Java 17 or higher (required by Spring Boot 3.x)
  - Spring Boot 3.0.x or higher
  - Jakarta Mail API (provided via spring-boot-starter-mail)

#### Auto-Configuration
- Auto-configuration remains fully functional
- Uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- `@AutoConfiguration` annotation for Spring Boot 3.x compatibility
- Conditional bean registration with `@ConditionalOnClass` and `@ConditionalOnMissingBean`

#### Build Verification
- Verified compilation with Spring Boot 3.x dependencies
- Verified Jakarta namespace usage (`jakarta.*` imports)
- All tests passing
- Clean build with no errors
- Compatible with Spring Framework 6.x

### Migration Guide

#### For Spring Boot 2.x Users
If you are using Spring Boot 2.x, you have two options:
1. **Stay on version 1.0.0** - Use `com.javaquery:spring-email:1.0.0` for Spring Boot 2.x support
2. **Upgrade to Spring Boot 3.x** - Upgrade your application to Spring Boot 3.x to use version 1.0.1

#### Upgrade Steps
1. Ensure your application uses Spring Boot 3.0.x or higher
2. Ensure your application uses Java 17 or higher
3. Update dependency to version 1.0.1
4. No code changes required in your application - API remains the same

### Notes

- **Breaking Change**: This version is NOT compatible with Spring Boot 2.x
- **API Compatibility**: Application code using this library remains unchanged
- **Configuration**: All configuration properties remain the same
- **Production Ready**: Fully tested and verified for production use with Spring Boot 3.x

## [1.0.0] - 2026-01-29

### Added

#### Core Features
- **Simple Email API** - Clean and intuitive email sending functionality
  - `MailService` interface for email operations
  - `SimpleMailService` implementation with full feature support
  - `EMailPayload` builder for composing email messages
- **Spring Boot Auto-Configuration** 
  - Automatic bean registration via `MailServiceAutoConfiguration`
  - Zero configuration setup - works out of the box
  - No manual component scanning required
  - Conditional activation based on dependencies
  - Respects custom `MailService` implementations
- **Multiple Content Types**
  - Plain text emails
  - HTML emails with full markup support
  - UTF-8 encoding by default
- **Recipient Management**
  - Primary recipients (TO)
  - Carbon copy (CC)
  - Blind carbon copy (BCC)
  - Support for multiple recipients per type
  - Automatic validation of recipient presence
- **Advanced Email Features**
  - Custom sender name ("From Name")
  - Reply-To address configuration
  - File attachments with multiple file support
  - Custom email headers via JavaMail
- **Configuration Control**
  - `spring.mail.enabled` property to enable/disable email sending
  - Environment-specific configuration support
  - Graceful handling when email is disabled

#### Email Composition
- **EMailPayload Builder**
  - `from(String)` - Set sender email address (required)
  - `fromName(String)` - Set friendly sender name (optional)
  - `replyTo(String)` - Set reply-to address (optional)
  - `to(Iterable<String>)` - Add primary recipients (at least one recipient type required)
  - `cc(Iterable<String>)` - Add CC recipients (optional)
  - `bcc(Iterable<String>)` - Add BCC recipients (optional)
  - `subject(String)` - Set email subject (required)
  - `body(String)` - Set email body content (required)
  - `isHtml(boolean)` - Specify if body is HTML (required)
  - `attachments(Iterable<File>)` - Attach files (optional)
  - `build()` - Create immutable payload object

#### Email Sending
- **SimpleMailService Implementation**
  - Constructor injection of `JavaMailSender`
  - Automatic MIME message creation
  - Character encoding handling (UTF-8)
  - Attachment processing with proper naming
  - Exception handling with descriptive error messages
  - Logging support via SLF4J
- **Smart Behavior**
  - Skips sending when no recipients specified (with warning log)
  - Skips sending when `spring.mail.enabled=false` (with debug log)
  - Validates recipients before attempting to send
  - Throws `RuntimeException` on failure with cause chain

#### Spring Boot Integration
- **Auto-Configuration**
  - `MailServiceAutoConfiguration` - Automatic bean registration
  - `META-INF/spring.factories` - Spring Boot 2.x auto-configuration registration
  - `@ConditionalOnClass` - Only activates when required classes present
  - `@ConditionalOnMissingBean` - Respects custom implementations
  - Runs after `MailSenderAutoConfiguration` for proper dependency order
  - Compatible with Spring Boot 2.7.x
- **Configuration Properties**
  - `spring.mail.enabled` - Custom property to control email sending (default: false)
  - All standard Spring Boot mail properties supported
  - Property value injection via `@Value` annotation

#### Testing Support
- **Comprehensive Test Coverage**
  - `SimpleMailServiceTest` - Integration tests with GreenMail
  - `MailServiceAutoConfigurationTest` - Auto-configuration verification tests
  - Tests for all email features (HTML, attachments, multiple recipients, etc.)
  - Verification of auto-configuration behavior
  - Validation that custom beans are respected
- **Test Utilities**
  - GreenMail integration for SMTP testing
  - Per-method lifecycle for test isolation
  - Temporary file handling for attachment tests

#### Documentation
- **Comprehensive README**
  - Feature overview with icons
  - Installation instructions (Gradle & Maven)
  - Configuration examples (YAML & Properties)
  - Common SMTP provider configurations (Gmail, Outlook, AWS SES)
  - Quick start guide
  - Multiple usage examples (plain text, HTML, attachments, etc.)
  - Advanced usage patterns (async, templates, error handling)
  - API reference with property tables
  - Best practices and troubleshooting guide

#### Dependencies
- **Core Dependencies**
  - Spring Boot Starter Mail (compileOnly)
  - Spring Boot Autoconfigure (implementation)
  - JavaMail API (transitive)
  - JLite Util module (api)
- **Build Tools**
  - Spring Boot Autoconfigure Processor (annotationProcessor)
  - Configuration metadata generation support
- **Test Dependencies**
  - Spring Boot Starter Mail (testImplementation)
  - GreenMail 1.6.15 (testImplementation)
  - GreenMail JUnit5 1.6.15 (testImplementation)
  - Spring Boot Starter Test (testImplementation)

### Technical Details

#### Architecture
- **Interface-Based Design**
  - `MailService` interface for flexibility
  - Easy to mock for unit testing
  - Supports custom implementations
- **Dependency Injection**
  - Constructor-based injection
  - No field injection (best practice)
  - Clear dependencies via constructor parameters
- **Builder Pattern**
  - Immutable `EMailPayload` objects
  - Type-safe email composition
  - Validation at build time

#### Error Handling
- **Comprehensive Exception Handling**
  - `RuntimeException` wrapping for checked exceptions
  - Descriptive error messages
  - Cause chain preservation
  - SLF4J logging at appropriate levels (WARN, DEBUG, ERROR)

#### Performance Considerations
- **Efficient Resource Usage**
  - Single `JavaMailSender` instance
  - Proper cleanup of MIME messages
  - No memory leaks in attachment handling
- **Async Support Ready**
  - Compatible with `@Async` annotation
  - Thread-safe implementation
  - No shared mutable state

#### Configuration Flexibility
- **Multiple Configuration Methods**
  - YAML configuration files
  - Properties files
  - Environment variables
  - Programmatic configuration
- **Profile-Based Configuration**
  - Different settings per environment
  - Easy to disable in development
  - Secure credential management

### Security Considerations
- **Authentication Support**
  - SMTP authentication
  - TLS/SSL encryption
  - STARTTLS support
- **Best Practices**
  - No hardcoded credentials
  - External configuration support
  - App-specific password recommendations in documentation

### Examples Provided
1. Plain text email
2. HTML email
3. Email with custom sender name
4. Email with Reply-To address
5. Multiple recipients
6. Email with CC and BCC
7. Email with attachments
8. Complete example with all features
9. Custom mail service implementation
10. Async email sending
11. Email templates
12. Environment-specific configuration
13. Error handling patterns

### Breaking Changes
- None (initial release)

### Migration Notes
- No migration needed (initial release)
- If upgrading from pre-1.0.0 versions that required manual component scanning:
  - Remove `@ComponentScan(basePackages = "com.javaquery.spring.mx")` from your application
  - Remove manual `@Bean` definitions for `MailService`
  - The module now auto-configures automatically

### Known Limitations
- Spring Boot 2.7.x support (uses javax.* namespace)
- Requires Java 11 or higher
- Requires `spring-boot-starter-mail` on classpath
- Email sending is disabled by default (`spring.mail.enabled=false`)
  - Must be explicitly enabled via configuration

### Deprecations
- None (initial release)

### Removed
- Manual `@Service` annotation from `SimpleMailService` (replaced with auto-configuration)
- `TestConfiguration` class (replaced with direct auto-configuration in tests)

### Contributors
- Vicky Thakor (@javaquery)

### Links
- [GitHub Repository](https://github.com/javaquery/JLite)
- [Documentation](README.md)

---

## Version History

### Release Schedule
- **1.0.1** - January 31, 2026 - Spring Boot 3.x support with Jakarta EE migration
- **1.0.0** - January 29, 2026 - Initial stable release with Spring Boot 2.x support

### Compatibility Matrix

| Module Version | Spring Boot | Java | Jakarta/Javax | Status |
|----------------|-------------|------|---------------|--------|
| **1.0.1** | 3.0.x - 3.5.x | 17+ | jakarta.* | ✅ Current |
| 1.0.0 | 2.7.x | 11+ | javax.* | ⚠️ Legacy |

### Future Roadmap
- Template engine integration (Thymeleaf, Freemarker)
- Email queue support
- Retry mechanism for failed sends
- Email tracking and analytics
- Bulk email sending with rate limiting
- Email validation utilities
- Calendar invite support (iCal)
- Inline image support
- Email preview/testing tools

---

*For detailed usage examples and API documentation, see [README.md](README.md)*

