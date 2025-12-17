package com.javaquery.spring.mx;

import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@Import(MailSenderAutoConfiguration.class)
public class TestConfiguration {

    @Bean
    public MailService mailService(JavaMailSender javaMailSender) {
        return new SimpleMailService(javaMailSender);
    }
}
