# Changelog

All notable changes to the module:spring-email module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
- **AUTO-CONFIGURATION.md**
  - Detailed auto-configuration guide
  - Before/after comparison
  - Technical implementation details
  - Migration guide from manual configuration
  - FAQ section
  - Benefits and usage examples

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
- Auto-configuration designed for Spring Boot 2.7.x
  - For Spring Boot 3.x, the `spring.factories` location needs updating
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
- [Auto-Configuration Guide](AUTO-CONFIGURATION.md)

---

## Version History

### Release Schedule
- **1.0.0** - January 29, 2026 - Initial stable release with auto-configuration

### Compatibility Matrix

| Module Version | Spring Boot | Java | Status |
|----------------|-------------|------|--------|
| 1.0.0 | 2.7.x | 11+ | ✅ Stable |

### Future Roadmap
- Spring Boot 3.x support
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

*For auto-configuration details, see [AUTO-CONFIGURATION.md](AUTO-CONFIGURATION.md)*
