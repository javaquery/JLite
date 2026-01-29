package com.javaquery.spring.aws.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaquery.spring.aws.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

/**
 * Test for AwsS3AutoConfiguration to verify auto-configuration works correctly.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
class AwsS3AutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AwsS3AutoConfiguration.class))
            .withPropertyValues("aws.accessKeyId=test-key", "aws.secretAccessKey=test-secret");

    @Test
    void shouldAutoConfigureS3Service() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(S3Service.class);
            assertThat(context).hasSingleBean(AwsCredentialsProvider.class);
            assertThat(context.getBean(S3Service.class)).isInstanceOf(S3Service.class);
        });
    }

    @Test
    void shouldNotAutoConfigureWhenS3ServiceBeanAlreadyExists() {
        contextRunner
                .withBean("customS3Service", S3Service.class, () -> new S3Service(null))
                .run(context -> {
                    assertThat(context).hasSingleBean(S3Service.class);
                    assertThat(context.getBean("customS3Service")).isInstanceOf(S3Service.class);
                });
    }

    @Test
    void shouldAutoConfigureWhenAwsCredentialsProviderIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("s3Service");
            S3Service s3Service = context.getBean(S3Service.class);
            assertThat(s3Service).isNotNull();
        });
    }

    @Test
    void shouldUseDefaultCredentialsProviderWhenNoCredentialsConfigured() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AwsS3AutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(AwsCredentialsProvider.class);
                    assertThat(context).hasSingleBean(S3Service.class);
                });
    }
}
