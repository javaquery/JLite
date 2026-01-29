package com.javaquery.spring.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.javaquery.util.Is;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Google Cloud Platform configuration for providing GoogleCredentials
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
public class GoogleCloudPlatform {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.credentials.file:}")
    private String credentialsFile;

    @Value("${firebase.credentials.string:}")
    private String credentialsString;

    /**
     * Constructor to inject ResourceLoader
     * @param resourceLoader the resource loader
     */
    public GoogleCloudPlatform(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Provides GoogleCredentials based on configuration
     * @return GoogleCredentials instance
     * @throws IOException if an I/O error occurs
     */
    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        if (Is.nonNullNonEmpty(credentialsFile)) {
            if (credentialsFile.startsWith("classpath:")) {
                Resource resource = resourceLoader.getResource(credentialsFile);
                try (InputStream inputStream = resource.getInputStream()) {
                    return GoogleCredentials.fromStream(inputStream);
                }
            } else {
                try (FileInputStream serviceAccountStream = new FileInputStream(credentialsFile)) {
                    return GoogleCredentials.fromStream(serviceAccountStream);
                }
            }
        } else if (Is.nonNullNonEmpty(credentialsString)) {
            try (InputStream inputStream =
                    new ByteArrayInputStream(credentialsString.getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }
        return GoogleCredentials.getApplicationDefault();
    }
}
