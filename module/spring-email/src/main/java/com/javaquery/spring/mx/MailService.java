package com.javaquery.spring.mx;

/**
 * Mail service interface for sending emails.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface MailService {
    /**
     * Send email based on the provided payload.
     *
     * @param eMailPayload the email payload containing details like recipients, subject, body, etc.
     */
    void send(EMailPayload eMailPayload);
}
