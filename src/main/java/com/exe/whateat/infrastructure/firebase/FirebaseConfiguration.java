package com.exe.whateat.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfiguration {

    @SuppressWarnings("java:S1075")
    private static final String SECRET_PATH = "secret/firebaseSecret.json";

    private static final String STORAGE_NAME = "whateat-9d316.appspot.com";

    public static final class FirebaseConfigurationException extends RuntimeException {

        public FirebaseConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static {
        try (final InputStream is = FirebaseConfiguration.class.getClassLoader()
                .getResourceAsStream(SECRET_PATH)) {
            if (is == null) {
                throw new NullPointerException("Cannot detect Firebase configuration.");
            }
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .setStorageBucket(STORAGE_NAME)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new FirebaseConfigurationException("Failed to load firebase configuration.", e);
        }
    }

    @Bean
    public StorageClient firebaseStorageClient() {
        return StorageClient.getInstance();
    }
}
