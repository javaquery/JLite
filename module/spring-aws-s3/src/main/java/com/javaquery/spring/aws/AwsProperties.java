package com.javaquery.spring.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS configuration properties.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {
    private String accessKeyId;
    private String secretAccessKey;
    private String providerName;
    private String accountId;
}
