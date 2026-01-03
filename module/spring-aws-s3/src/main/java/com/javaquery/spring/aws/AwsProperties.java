package com.javaquery.spring.aws;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "aws")
@Getter
public class AwsProperties {
    private String accessKeyId;
    private String secretAccessKey;
    private String providerName;
    private String accountId;
}
