package com.javaquery.spring.aws;

import com.javaquery.util.Is;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Provides AWS Credentials Provider bean.
 * - If accessKeyId or secretAccessKey is not provided, it uses DefaultCredentialsProvider. (Service based credentials resolution)
 * - If both accessKeyId and secretAccessKey are provided, it creates StaticCredentialsProvider with the given credentials.
 *
 * @author javaquery
 * @since 1.0.0
 */
@Configuration
public class AmazonWebServices {

    private final AwsProperties awsProperties;

    /**
     * Constructor to initialize AwsProperties.
     *
     * @param awsProperties the AWS properties
     */
    public AmazonWebServices(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    /**
     * Creates an AwsCredentialsProvider bean based on the provided AWS properties.
     *
     * @return AwsCredentialsProvider instance
     */
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (Is.nullOrEmpty(awsProperties.getAccessKeyId()) || Is.nullOrEmpty(awsProperties.getSecretAccessKey())) {
            return DefaultCredentialsProvider.builder().build();
        } else {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.builder()
                    .accessKeyId(awsProperties.getAccessKeyId())
                    .secretAccessKey(awsProperties.getSecretAccessKey())
                    .providerName(awsProperties.getProviderName())
                    .accountId(awsProperties.getAccountId())
                    .build();
            return StaticCredentialsProvider.create(awsCreds);
        }
    }
}
