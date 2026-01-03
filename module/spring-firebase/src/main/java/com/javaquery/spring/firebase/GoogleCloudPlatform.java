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

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
public class GoogleCloudPlatform {

    @Value("${firebase.credentials.file:}")
    private String credentialsFile;

    @Value("${firebase.credentials.string:}")
    private String credentialsString;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        if (Is.nonNullNonEmpty(credentialsFile)) {
            try (FileInputStream serviceAccountStream = new FileInputStream(credentialsFile)) {
                return GoogleCredentials.fromStream(serviceAccountStream);
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
