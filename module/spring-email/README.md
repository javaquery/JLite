# Spring Email Module

A lightweight and easy-to-use Spring Boot email module that simplifies sending emails with support for HTML content, attachments, multiple recipients, and more. Built on top of Spring Boot's mail starter.

> **✨ Spring Boot 3.x+ Required** | **☕ Java 17+** | **📦 Jakarta EE Compatible**

> **⚠️ Breaking Change in v1.0.1**: This version requires Spring Boot 3.x or above due to Jakarta EE migration. For Spring Boot 2.x support, use version 1.0.0.

## Features

- 📧 **Simple API** - Clean and intuitive builder pattern for email composition
- 🎨 **HTML Support** - Send both plain text and HTML emails
- 📎 **Attachments** - Attach multiple files to your emails
- 👥 **Multiple Recipients** - Support for TO, CC, and BCC recipients
- 🔄 **Reply-To Support** - Configure custom reply-to addresses
- 🎭 **Custom Sender Name** - Set a friendly name for the sender
- ⚙️ **Configuration Toggle** - Enable/disable email sending via configuration
- 🧪 **Testable** - Easy to test with GreenMail or similar tools
- 🛡️ **Safe Defaults** - Gracefully handles missing recipients and configuration

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javaquery:spring-email:1.0.1'
    implementation 'org.springframework.boot:spring-boot-starter-mail'
}
```

### Maven

```xml
<dependency>
    <groupId>com.javaquery</groupId>
    <artifactId>spring-email</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

> **✨ Auto-Configuration**: This module uses Spring Boot auto-configuration. Simply add the dependency and the `MailService` bean will be automatically available for injection - no manual configuration or component scanning required!

## Compatibility & Migration

### Version Compatibility Matrix

| Library Version | Spring Boot | Java | Jakarta/Javax | Status |
|----------------|-------------|------|---------------|--------|
| **1.0.1** | 3.0.x - 3.5.x | 17+ | jakarta.* | ✅ Current |
| 1.0.0 | 2.7.x | 11+ | javax.* | ⚠️ Legacy |

### Migration from Spring Boot 2.x to 3.x

#### If You're Using Spring Boot 2.x
You have two options:
1. **Stay on version 1.0.0** - Continue using `com.javaquery:spring-email:1.0.0`
2. **Upgrade to Spring Boot 3.x** - Upgrade your application and use version 1.0.1

#### Upgrade Steps (to version 1.0.1)
1. Upgrade your application to Spring Boot 3.0.x or higher
2. Upgrade to Java 17 or higher
3. Update dependency version to 1.0.1
4. **No code changes required** - The API remains identical

#### What Changed in 1.0.1
- ✅ Uses `jakarta.mail.*` instead of `javax.mail.*`
- ✅ Compatible with Spring Boot 3.x (3.0.x through 3.5.x)
- ✅ Requires Java 17+
- ✅ Same API - no code changes in your application
- ❌ Not compatible with Spring Boot 2.x

## Configuration

Add mail configuration to your `application.yml` or `application.properties`:

### application.yml

```yaml
spring:
  mail:
    enabled: true                    # Enable/disable email sending (default: false)
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

### application.properties

```properties
spring.mail.enabled=true
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### Common SMTP Providers

#### Gmail

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password  # Use App Password, not regular password
```

#### Outlook/Office 365

```yaml
spring:
  mail:
    host: smtp.office365.com
    port: 587
    username: your-email@outlook.com
    password: your-password
```

#### AWS SES

```yaml
spring:
  mail:
    host: email-smtp.us-east-1.amazonaws.com
    port: 587
    username: your-ses-smtp-username
    password: your-ses-smtp-password
```

## Quick Start

### Basic Usage

```java
import com.javaquery.spring.mx.MailService;
import com.javaquery.spring.mx.EMailPayload;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    
    private final MailService mailService;
    
    public NotificationService(MailService mailService) {
        this.mailService = mailService;
    }
    
    public void sendWelcomeEmail(String recipientEmail, String userName) {
        EMailPayload payload = EMailPayload.builder()
            .from("noreply@example.com")
            .to(List.of(recipientEmail))
            .subject("Welcome to Our Service!")
            .body("Hello " + userName + ", welcome to our platform!")
            .isHtml(false)
            .build();
        
        mailService.send(payload);
    }
}
```

## Examples

### Plain Text Email

```java
EMailPayload payload = EMailPayload.builder()
    .from("sender@example.com")
    .to(List.of("recipient@example.com"))
    .subject("Plain Text Email")
    .body("This is a plain text email.")
    .isHtml(false)
    .build();

mailService.send(payload);
```

### HTML Email

```java
String htmlContent = """
    <html>
    <body>
        <h1>Welcome!</h1>
        <p>Thank you for registering on our platform.</p>
        <a href="https://example.com/verify">Click here to verify your email</a>
    </body>
    </html>
    """;

EMailPayload payload = EMailPayload.builder()
    .from("noreply@example.com")
    .to(List.of("user@example.com"))
    .subject("Please Verify Your Email")
    .body(htmlContent)
    .isHtml(true)
    .build();

mailService.send(payload);
```

### Email with Custom Sender Name

```java
EMailPayload payload = EMailPayload.builder()
    .from("noreply@example.com")
    .fromName("Example Corp Support")
    .to(List.of("customer@example.com"))
    .subject("Your Support Ticket")
    .body("Your support ticket has been received.")
    .isHtml(false)
    .build();

mailService.send(payload);
```

### Email with Reply-To

```java
EMailPayload payload = EMailPayload.builder()
    .from("noreply@example.com")
    .replyTo("support@example.com")
    .to(List.of("customer@example.com"))
    .subject("Support Request")
    .body("Please reply to this email if you need further assistance.")
    .isHtml(false)
    .build();

mailService.send(payload);
```

### Multiple Recipients

```java
EMailPayload payload = EMailPayload.builder()
    .from("admin@example.com")
    .to(List.of("user1@example.com", "user2@example.com", "user3@example.com"))
    .subject("Team Announcement")
    .body("This is an important team announcement.")
    .isHtml(false)
    .build();

mailService.send(payload);
```

### Email with CC and BCC

```java
EMailPayload payload = EMailPayload.builder()
    .from("manager@example.com")
    .to(List.of("employee@example.com"))
    .cc(List.of("supervisor@example.com", "hr@example.com"))
    .bcc(List.of("archive@example.com"))
    .subject("Performance Review")
    .body("Your performance review is attached.")
    .isHtml(false)
    .build();

mailService.send(payload);
```

### Email with Attachments

```java
File document = new File("/path/to/document.pdf");
File image = new File("/path/to/image.png");

EMailPayload payload = EMailPayload.builder()
    .from("sender@example.com")
    .to(List.of("recipient@example.com"))
    .subject("Documents Attached")
    .body("Please find the attached documents.")
    .isHtml(false)
    .attachments(List.of(document, image))
    .build();

mailService.send(payload);
```

### Complete Example with All Features

```java
File report = new File("/path/to/monthly-report.pdf");

String htmlBody = """
    <html>
    <body>
        <h2>Monthly Report</h2>
        <p>Dear Team,</p>
        <p>Please find attached the monthly report for your review.</p>
        <ul>
            <li>Sales: $100,000</li>
            <li>Growth: 15%</li>
            <li>New Customers: 250</li>
        </ul>
        <p>Best regards,<br/>Management Team</p>
    </body>
    </html>
    """;

EMailPayload payload = EMailPayload.builder()
    .from("reports@example.com")
    .fromName("Example Corp Reports")
    .replyTo("management@example.com")
    .to(List.of("team@example.com"))
    .cc(List.of("ceo@example.com", "cfo@example.com"))
    .bcc(List.of("archive@example.com"))
    .subject("Monthly Report - December 2025")
    .body(htmlBody)
    .isHtml(true)
    .attachments(List.of(report))
    .build();

mailService.send(payload);
```

## Advanced Usage

### Custom Mail Service Implementation

You can create your own implementation of `MailService` for custom behavior:

```java
import com.javaquery.spring.mx.MailService;
import com.javaquery.spring.mx.EMailPayload;
import org.springframework.stereotype.Service;

@Service
public class CustomMailService implements MailService {
    
    @Override
    public void send(EMailPayload eMailPayload) {
        // Add custom logic: logging, queuing, rate limiting, etc.
        logEmailAttempt(eMailPayload);
        
        // Implement your custom sending logic
        // Or delegate to SimpleMailService
    }
    
    private void logEmailAttempt(EMailPayload payload) {
        // Log email sending attempts
    }
}
```

### Async Email Sending

For better performance, send emails asynchronously:

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncEmailService {
    
    private final MailService mailService;
    
    public AsyncEmailService(MailService mailService) {
        this.mailService = mailService;
    }
    
    @Async
    public void sendEmailAsync(EMailPayload payload) {
        mailService.send(payload);
    }
}
```

Don't forget to enable async support:

```java
@SpringBootApplication
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Email Templates

Create reusable email templates:

```java
@Service
public class EmailTemplateService {
    
    private final MailService mailService;
    
    public EmailTemplateService(MailService mailService) {
        this.mailService = mailService;
    }
    
    public void sendWelcomeEmail(User user) {
        String htmlBody = buildWelcomeTemplate(user);
        
        EMailPayload payload = EMailPayload.builder()
            .from("welcome@example.com")
            .fromName("Example Corp")
            .to(List.of(user.getEmail()))
            .subject("Welcome to Example Corp!")
            .body(htmlBody)
            .isHtml(true)
            .build();
        
        mailService.send(payload);
    }
    
    public void sendPasswordResetEmail(User user, String resetToken) {
        String htmlBody = buildPasswordResetTemplate(user, resetToken);
        
        EMailPayload payload = EMailPayload.builder()
            .from("security@example.com")
            .fromName("Example Corp Security")
            .to(List.of(user.getEmail()))
            .subject("Password Reset Request")
            .body(htmlBody)
            .isHtml(true)
            .build();
        
        mailService.send(payload);
    }
    
    private String buildWelcomeTemplate(User user) {
        return """
            <html>
            <body>
                <h1>Welcome %s!</h1>
                <p>We're excited to have you on board.</p>
            </body>
            </html>
            """.formatted(user.getName());
    }
    
    private String buildPasswordResetTemplate(User user, String token) {
        return """
            <html>
            <body>
                <h2>Password Reset Request</h2>
                <p>Click the link below to reset your password:</p>
                <a href="https://example.com/reset?token=%s">Reset Password</a>
            </body>
            </html>
            """.formatted(token);
    }
}
```

### Environment-Specific Configuration

Disable email sending in development:

```yaml
# application-dev.yml
spring:
  mail:
    enabled: false  # Emails won't be sent in development

# application-prod.yml
spring:
  mail:
    enabled: true   # Emails will be sent in production
```

### Error Handling

```java
@Service
public class SafeEmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(SafeEmailService.class);
    private final MailService mailService;
    
    public SafeEmailService(MailService mailService) {
        this.mailService = mailService;
    }
    
    public boolean sendWithErrorHandling(EMailPayload payload) {
        try {
            mailService.send(payload);
            logger.info("Email sent successfully to: {}", payload.getTo());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", payload.getTo(), e);
            return false;
        }
    }
}
```

## API Reference

### EMailPayload

Builder class for composing email messages.

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `from` | `String` | Yes | Sender email address |
| `fromName` | `String` | No | Friendly name for sender |
| `replyTo` | `String` | No | Reply-to email address |
| `to` | `Iterable<String>` | No* | Primary recipients |
| `cc` | `Iterable<String>` | No | CC recipients |
| `bcc` | `Iterable<String>` | No | BCC recipients |
| `subject` | `String` | Yes | Email subject |
| `body` | `String` | Yes | Email body content |
| `isHtml` | `boolean` | Yes | Whether body is HTML |
| `attachments` | `Iterable<File>` | No | File attachments |

*At least one recipient (to, cc, or bcc) must be provided.

### MailService

Interface with a single method:

```java
void send(EMailPayload eMailPayload);
```

### SimpleMailService

Default implementation of `MailService` that:
- Respects `spring.mail.enabled` configuration
- Handles multiple recipient types
- Supports attachments
- Validates recipients before sending
- Throws `RuntimeException` on failure

## Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `spring.mail.enabled` | `boolean` | `false` | Enable/disable email sending |
| `spring.mail.host` | `String` | - | SMTP server host |
| `spring.mail.port` | `int` | - | SMTP server port |
| `spring.mail.username` | `String` | - | SMTP username |
| `spring.mail.password` | `String` | - | SMTP password |
| `spring.mail.properties.*` | `Map` | - | Additional JavaMail properties |

## Best Practices

1. **Use App Passwords**: For Gmail and similar providers, use app-specific passwords instead of regular passwords

2. **Enable/Disable Toggle**: Use `spring.mail.enabled` to control email sending per environment

3. **Async Processing**: Send emails asynchronously to avoid blocking the main thread

4. **Error Handling**: Always wrap email sending in try-catch blocks for production code

5. **Template Management**: Create reusable email templates for consistent branding

6. **Testing**: Use GreenMail or similar tools for integration testing

7. **Rate Limiting**: Implement rate limiting for bulk email sending

8. **Logging**: Log email attempts and failures for debugging

## Troubleshooting

### Emails Not Being Sent

1. Check `spring.mail.enabled` is set to `true`
2. Verify SMTP credentials are correct
3. Check firewall/network restrictions on SMTP port
4. Review application logs for error messages

### Authentication Failed

1. Use app-specific passwords (Gmail, Yahoo, etc.)
2. Enable "Less secure app access" (not recommended)
3. Check username format (full email vs username only)

### Emails Go to Spam

1. Configure SPF, DKIM, and DMARC records
2. Use a verified sender domain
3. Include unsubscribe links in marketing emails
4. Avoid spam trigger words

### Connection Timeout

1. Check SMTP host and port
2. Verify network/firewall settings
3. Increase timeout values in configuration

## Requirements

- **Java 17 or higher** (required by Spring Boot 3.x)
- **Spring Boot 3.0.x or higher** (3.0.x, 3.1.x, 3.2.x, 3.3.x, 3.4.x, 3.5.x)
- **Spring Boot Mail Starter** (with Jakarta Mail API)

> **Note**: For Spring Boot 2.x support, use version 1.0.0 of this library.

## License

This module is part of the JLite project. See LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or contributions, please visit the [GitHub repository](https://github.com/javaquery/JLite).

## Author

**Vicky Thakor**  
JavaQuery

---

**Version**: 1.0.1  
**Last Updated**: January 31, 2026  
**Spring Boot**: 3.x+ Required  
**Java**: 17+ Required

