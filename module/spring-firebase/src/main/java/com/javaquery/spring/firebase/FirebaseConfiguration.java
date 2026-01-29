package com.javaquery.spring.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.javaquery.util.Is;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Firebase configuration for initializing FirebaseApp
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
@Configuration
public class FirebaseConfiguration {

    @Value("${firebase.database.url:}")
    private String databaseUrl;

    private final GoogleCredentials googleCredentials;

    /**
     * Constructor to inject GoogleCredentials
     *
     * @param googleCredentials the Google credentials for Firebase
     */
    public FirebaseConfiguration(GoogleCredentials googleCredentials) {
        this.googleCredentials = googleCredentials;
    }

    /**
     * Initialize FirebaseApp if not already initialized
     */
    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder().setCredentials(googleCredentials);
            if (Is.nonNullNonEmpty(databaseUrl)) {
                optionsBuilder.setDatabaseUrl(databaseUrl);
            }
            FirebaseApp.initializeApp(optionsBuilder.build());
        }
    }
}
