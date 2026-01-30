package com.javaquery.spring.aws.config;

import com.javaquery.spring.aws.AmazonWebServices;
import com.javaquery.spring.aws.AwsProperties;
import com.javaquery.spring.aws.S3Service;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;

/**
 * Auto-configuration for AWS S3 Service.
 * This configuration will automatically be picked up by Spring Boot when the module is on the classpath.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@AutoConfiguration
@ConditionalOnClass({S3AsyncClient.class, AwsCredentialsProvider.class})
@EnableConfigurationProperties(AwsProperties.class)
@Import(AmazonWebServices.class)
public class AwsS3AutoConfiguration {

    /**
     * Creates an S3Service bean if one doesn't already exist.
     * This bean will only be created if AwsCredentialsProvider is available in the context.
     *
     * @param awsCredentialsProvider the AWS credentials provider bean
     * @return S3Service instance
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Service s3Service(AwsCredentialsProvider awsCredentialsProvider) {
        return new S3Service(awsCredentialsProvider);
    }
}
