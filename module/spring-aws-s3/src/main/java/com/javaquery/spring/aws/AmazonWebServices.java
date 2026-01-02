package com.javaquery.spring.aws;

import com.javaquery.util.Is;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
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
@Component
public class AmazonWebServices {

    @Value("${aws.accessKeyId:null}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey:null}")
    private String secretAccessKey;

    @Value("${aws.providerName:null}")
    private String providerName;

    @Value("${aws.accountId:null}")
    private String accountId;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (Is.nullOrEmpty(accessKeyId) || Is.nullOrEmpty(secretAccessKey)) {
            return DefaultCredentialsProvider.builder().build();
        } else {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.builder()
                    .accessKeyId(accessKeyId)
                    .secretAccessKey(secretAccessKey)
                    .providerName(providerName)
                    .accountId(accountId)
                    .build();
            return StaticCredentialsProvider.create(awsCreds);
        }
    }
}
