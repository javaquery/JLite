package com.javaquery.spring.mx.config;

import com.javaquery.spring.mx.MailService;
import com.javaquery.spring.mx.SimpleMailService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Autoconfiguration for Mail Service.
 * This configuration will automatically be picked up by Spring Boot when the module is on the classpath.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@ConditionalOnClass({JavaMailSender.class, MailService.class})
public class MailServiceAutoConfiguration {

    /**
     * Creates a MailService bean if one doesn't already exist.
     * This bean will only be created if JavaMailSender is available in the context.
     *
     * @param javaMailSender the JavaMailSender bean (autoconfigured by Spring Boot)
     * @return MailService implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public MailService mailService(JavaMailSender javaMailSender) {
        return new SimpleMailService(javaMailSender);
    }
}
