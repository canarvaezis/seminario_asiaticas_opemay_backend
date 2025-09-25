package co.edu.uniajc.estudiante.opemay.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
class FirebaseInitializer {

    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initFirestore() throws IOException {
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_03");

        InputStream serviceAccount;
        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        } else {
            this.firebaseInitialized = false;
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                this.firebaseInitialized = true;
            } catch (Exception e) {
                this.firebaseInitialized = false;
            }
        } else {
            this.firebaseInitialized = true;
        }
    }

    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            return null; // O puedes lanzar una excepción personalizada
        }

        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            return null;
        }
    }
}
