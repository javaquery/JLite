package com.javaquery.spring.mx;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class MimeMessageHelper {

    public static String extractBody(MimeMessage mimeMessage) throws MessagingException, IOException {
        Object content = mimeMessage.getContent();

        if (content instanceof String) {
            return (String) content;
        }

        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            return extractFromMultipart(multipart);
        }
        return "";
    }

    private static String extractFromMultipart(MimeMultipart multipart) throws MessagingException, IOException {

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                continue; // skip attachments
            }

            if (part.isMimeType("text/plain")) {
                return part.getContent().toString();
            }

            if (part.isMimeType("text/html")) {
                return part.getContent().toString();
            }

            if (part.getContent() instanceof MimeMultipart) {
                MimeMultipart nested = (MimeMultipart) part.getContent();
                String result = extractFromMultipart(nested);
                if (!result.isEmpty()) return result;
            }
        }
        return "";
    }
}
