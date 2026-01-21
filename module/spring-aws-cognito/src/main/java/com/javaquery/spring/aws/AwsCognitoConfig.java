package com.javaquery.spring.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * Provides CognitoIdentityProviderClient bean configured with the AWS region from AwsCognitoProperties.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class AwsCognitoConfig {

    private final AwsCognitoProperties awsCognitoProperties;

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(awsCognitoProperties.getRegion()))
                .build();
    }
}
