package com.javaquery.spring.aws;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Aws Cognito configuration properties.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "aws.cognito")
@Getter
public class AwsCognitoProperties {
    private String userPoolId;
    private String clientId;
    private String clientSecret;
    private String region;
    private String domain;
    private boolean allowDuplicateEmails = false;
}
