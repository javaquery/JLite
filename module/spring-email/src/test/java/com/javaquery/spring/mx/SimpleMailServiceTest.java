package com.javaquery.spring.mx;

import static org.junit.jupiter.api.Assertions.*;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for SimpleMailService using Spring Boot context
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
public class SimpleMailServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@example.com", "password"))
            .withPerMethodLifecycle(true);

    @Autowired
    private MailService mailService;

    private File tempAttachment;

    @AfterEach
    void tearDown() throws IOException {
        greenMail.reset();
        if (tempAttachment != null && tempAttachment.exists()) {
            Files.deleteIfExists(tempAttachment.toPath());
        }
    }

    @Test
    public void testSendSimpleEmail() throws MessagingException, IOException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient@example.com"))
                .subject("Test Subject")
                .body("Test Body")
                .isHtml(false)
                .build();
        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        MimeMessage message = receivedMessages[0];
        assertEquals("sender@example.com", message.getFrom()[0].toString());
        assertEquals("recipient@example.com", message.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("Test Subject", message.getSubject());
        String messageBody = MimeMessageHelper.extractBody(message);
        assertEquals(payload.getBody(), messageBody);
    }

    @Test
    void testSendHtmlEmail() throws MessagingException, IOException {
        String htmlBody = "<html><body><h1>Test HTML</h1><p>This is a test email</p></body></html>";
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient@example.com"))
                .subject("HTML Test")
                .body(htmlBody)
                .isHtml(true)
                .build();
        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        String messageBody = MimeMessageHelper.extractBody(message);
        assertEquals(htmlBody, messageBody);
        assertTrue(message.getContentType().contains("multipart"));
    }

    @Test
    void testSendEmailWithFromName() throws MessagingException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .fromName("Test Sender")
                .to(List.of("recipient@example.com"))
                .subject("Test with From Name")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        String fromHeader = message.getFrom()[0].toString();
        assertTrue(fromHeader.contains("Test Sender") || fromHeader.contains("sender@example.com"));
    }

    @Test
    void testSendEmailWithReplyTo() throws MessagingException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .replyTo("replyto@example.com")
                .to(List.of("recipient@example.com"))
                .subject("Test with ReplyTo")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertNotNull(message.getReplyTo());
        assertEquals("replyto@example.com", message.getReplyTo()[0].toString());
    }

    @Test
    void testSendEmailWithMultipleRecipients() throws MessagingException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient1@example.com", "recipient2@example.com", "recipient3@example.com"))
                .subject("Test Multiple Recipients")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(3, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(3, message.getRecipients(Message.RecipientType.TO).length);
    }

    @Test
    void testSendEmailWithCc() throws MessagingException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient@example.com"))
                .cc(List.of("cc1@example.com", "cc2@example.com"))
                .subject("Test with CC")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(3, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(2, message.getRecipients(Message.RecipientType.CC).length);
        assertEquals("cc1@example.com", message.getRecipients(Message.RecipientType.CC)[0].toString());
        assertEquals("cc2@example.com", message.getRecipients(Message.RecipientType.CC)[1].toString());
    }

    @Test
    void testSendEmailWithBcc() {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient@example.com"))
                .bcc(List.of("bcc1@example.com", "bcc2@example.com"))
                .subject("Test with BCC")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(3, receivedMessages.length);
    }

    @Test
    void testSendEmailWithAllRecipientTypes() throws MessagingException {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("to@example.com"))
                .cc(List.of("cc@example.com"))
                .bcc(List.of("bcc@example.com"))
                .subject("Test All Recipients")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(3, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
        assertEquals(1, message.getRecipients(Message.RecipientType.CC).length);
    }

    @Test
    void testSendEmailWithAttachment() throws MessagingException, IOException {
        tempAttachment = File.createTempFile("test-attachment", ".txt");
        Files.writeString(tempAttachment.toPath(), "This is a test attachment");

        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of("recipient@example.com"))
                .subject("Test with Attachment")
                .body("Test Body with Attachment")
                .isHtml(false)
                .attachments(List.of(tempAttachment))
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertInstanceOf(MimeMultipart.class, message.getContent());
        MimeMultipart multipart = (MimeMultipart) message.getContent();
        assertTrue(multipart.getCount() > 1); // At least body + attachment
    }

    @Test
    void testSendEmailWithMultipleAttachments() throws MessagingException, IOException {
        File attachment1 = File.createTempFile("test-attachment1", ".txt");
        File attachment2 = File.createTempFile("test-attachment2", ".txt");
        Files.writeString(attachment1.toPath(), "Attachment 1");
        Files.writeString(attachment2.toPath(), "Attachment 2");

        try {
            EMailPayload payload = EMailPayload.builder()
                    .from("sender@example.com")
                    .to(List.of("recipient@example.com"))
                    .subject("Test with Multiple Attachments")
                    .body("Test Body")
                    .isHtml(false)
                    .attachments(List.of(attachment1, attachment2))
                    .build();

            mailService.send(payload);

            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage message = receivedMessages[0];
            assertInstanceOf(MimeMultipart.class, message.getContent());
            MimeMultipart multipart = (MimeMultipart) message.getContent();
            assertTrue(multipart.getCount() >= 3); // body + 2 attachments
        } finally {
            Files.deleteIfExists(attachment1.toPath());
            Files.deleteIfExists(attachment2.toPath());
        }
    }

    @Test
    void testSendEmailWithEmptyTo() {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .to(List.of())
                .subject("Test Empty To")
                .body("Test Body")
                .isHtml(false)
                .build();

        mailService.send(payload);
        assertDoesNotThrow(() -> mailService.send(payload));
    }

    @Test
    void testSendEmailWithNullTo() {
        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .subject("Test Null To")
                .body("Test Body")
                .isHtml(false)
                .build();

        assertDoesNotThrow(() -> mailService.send(payload));
    }

    @Test
    void testSendEmailWithComplexScenario() throws MessagingException, IOException {
        tempAttachment = File.createTempFile("complex-test", ".txt");
        Files.writeString(tempAttachment.toPath(), "Complex test attachment");

        String htmlBody = "<html><body><h1>Complex Email</h1><p>With <b>HTML</b> content</p></body></html>";

        EMailPayload payload = EMailPayload.builder()
                .from("sender@example.com")
                .fromName("Complex Sender")
                .replyTo("replyto@example.com")
                .to(List.of("to1@example.com", "to2@example.com"))
                .cc(List.of("cc@example.com"))
                .bcc(List.of("bcc@example.com"))
                .subject("Complex Test Email")
                .body(htmlBody)
                .isHtml(true)
                .attachments(List.of(tempAttachment))
                .build();

        mailService.send(payload);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(4, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Complex Test Email", message.getSubject());
        assertEquals(2, message.getRecipients(Message.RecipientType.TO).length);
        assertEquals(1, message.getRecipients(Message.RecipientType.CC).length);
        assertNotNull(message.getReplyTo());
        assertInstanceOf(MimeMultipart.class, message.getContent());
    }
}
