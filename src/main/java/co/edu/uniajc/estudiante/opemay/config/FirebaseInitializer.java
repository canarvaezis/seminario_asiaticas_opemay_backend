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

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
@Slf4j
class FirebaseInitializer {

    private boolean firebaseInitialized = false;

 @PostConstruct
    public void initFirestore() throws IOException {
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_03");

        InputStream serviceAccount;
        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            // Producción/GitHub Actions: usar variable de entorno
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
            log.info("Variable de entorno FIREBASE_CONFIG_03 encontrada, inicializando Firebase...");
        } else {
            // Desarrollo local: usar archivo firebase-key.json
            serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");
            if (serviceAccount == null) {
                log.warn("No se encontró firebase-key.json en resources. Firebase no será inicializado.");
                this.firebaseInitialized = false;
                return;
            }
            log.info("Usando firebase-key.json para desarrollo local, inicializando Firebase...");
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                this.firebaseInitialized = true;
                log.info("Firebase inicializado exitosamente.");
            } catch (Exception e) {
                log.error("Error inicializando Firebase: {}", e.getMessage());
                this.firebaseInitialized = false;
            }
        } else {
            this.firebaseInitialized = true;
            log.info("Firebase ya estaba inicializado.");
        }
    }

    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            // Para desarrollo local cuando Firebase no está configurado
            // En producción/CI, las variables de entorno estarán disponibles
            log.warn("Firebase no inicializado. Retornando null para desarrollo local.");
            return null;
        }

        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            log.error("Error obteniendo Firestore client: {}", e.getMessage());
            return null;
        }
    }
}
