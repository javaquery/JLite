package com.javaquery.spring.mx.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaquery.spring.mx.MailService;
import com.javaquery.spring.mx.SimpleMailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Test for MailServiceAutoConfiguration to verify autoconfiguration works correctly.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
class MailServiceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(MailSenderAutoConfiguration.class, MailServiceAutoConfiguration.class))
            .withPropertyValues("spring.mail.host=localhost", "spring.mail.port=3025");

    @Test
    void shouldAutoConfigureMailService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MailService.class);
            assertThat(context).hasSingleBean(JavaMailSender.class);
            assertThat(context.getBean(MailService.class)).isInstanceOf(SimpleMailService.class);
        });
    }

    @Test
    void shouldNotAutoConfigureWhenMailServiceBeanAlreadyExists() {
        contextRunner
                .withBean("customMailService", MailService.class, () -> new SimpleMailService(null))
                .run(context -> {
                    assertThat(context).hasSingleBean(MailService.class);
                    assertThat(context.getBean("customMailService")).isInstanceOf(MailService.class);
                });
    }

    @Test
    void shouldAutoConfigureWhenJavaMailSenderIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("mailService");
            MailService mailService = context.getBean(MailService.class);
            assertThat(mailService).isNotNull();
        });
    }
}
