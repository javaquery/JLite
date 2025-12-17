package com.javaquery.spring.mx;

import com.javaquery.util.Is;
import com.javaquery.util.Strings;
import com.javaquery.util.collection.Arrays;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author vicky.thakor
 * @since 2025-12-17
 */
@Service
public class SimpleMailService implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMailService.class);

    @Value("${spring.mail.enabled:false}")
    private boolean enabled;

    private final JavaMailSender emailSender;

    public SimpleMailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void send(EMailPayload eMailPayload) {
        if (enabled) {
            try {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

                if (Strings.nonNullNonEmpty(eMailPayload.getFrom())) {
                    mimeMessageHelper.setFrom(eMailPayload.getFrom(), eMailPayload.getFromName());
                } else {
                    mimeMessageHelper.setFrom(eMailPayload.getFrom());
                }
                Is.nonNullNonEmpty(eMailPayload.getReplyTo(), () -> {
                    try {
                        mimeMessageHelper.setReplyTo(eMailPayload.getReplyTo());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                Is.nonNullNonEmpty(eMailPayload.getTo(), () -> {
                    String[] toArray = Arrays.toArray(eMailPayload.getTo(), new String[0]);
                    try {
                        mimeMessageHelper.setTo(toArray);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                Is.nonNullNonEmpty(eMailPayload.getCc(), () -> {
                    String[] ccArray = Arrays.toArray(eMailPayload.getCc(), new String[0]);
                    try {
                        mimeMessageHelper.setCc(ccArray);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                Is.nonNullNonEmpty(eMailPayload.getBcc(), () -> {
                    String[] bccArray = Arrays.toArray(eMailPayload.getBcc(), new String[0]);
                    try {
                        mimeMessageHelper.setBcc(bccArray);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                mimeMessageHelper.setSubject(eMailPayload.getSubject());
                mimeMessageHelper.setText(eMailPayload.getBody(), eMailPayload.isHtml());

                Is.nonNullNonEmpty(eMailPayload.getAttachments(), () -> {
                    for (var attachment : eMailPayload.getAttachments()) {
                        try {
                            mimeMessageHelper.addAttachment(attachment.getName(), attachment);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                emailSender.send(mimeMessage);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
