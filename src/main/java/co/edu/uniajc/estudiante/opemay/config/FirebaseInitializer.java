package co.edu.uniajc.estudiante.opemay.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
        InputStream serviceAccount = null;

        // 1. Intentar con variable de entorno
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_03");
        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        } else {
            // 2. Si no hay variable, buscar en resources
            try {
                serviceAccount = new ClassPathResource("firebase-key.json").getInputStream();
            } catch (IOException e) {
                this.firebaseInitialized = false;
                throw new IllegalStateException("No se encontró configuración de Firebase (ni variable FIREBASE_CONFIG_03 ni resources/firebase-key.json)", e);
            }
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                this.firebaseInitialized = true;
            } catch (IOException e) {
                this.firebaseInitialized = false;
                throw new IllegalStateException("Error al inicializar Firebase", e);
            }
        } else {
            this.firebaseInitialized = true;
        }
    }

    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            throw new IllegalStateException("Firebase no inicializado. Verifica tu configuración.");
        }
        return FirestoreClient.getFirestore();
    }
}
