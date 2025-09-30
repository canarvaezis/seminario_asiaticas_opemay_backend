package co.edu.uniajc.estudiante.opemay.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
        String firebaseKeyPath = System.getenv("FIREBASE_KEY_PATH");

        InputStream serviceAccount;

        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            // Producción/GitHub Actions: usar variable de entorno con JSON inline
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
            log.info("✅ Usando FIREBASE_CONFIG_03 para inicializar Firebase...");
        } else if (firebaseKeyPath != null && !firebaseKeyPath.isBlank()) {
            // Producción local/EC2: usar archivo externo montado
            serviceAccount = new FileInputStream(firebaseKeyPath);
            log.info("✅ Usando archivo externo en {}", firebaseKeyPath);
        } else {
            // Desarrollo local: usar archivo en resources
            serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");
            if (serviceAccount == null) {
                log.warn("⚠️ No se encontró firebase-key.json ni variables de entorno. Firebase no será inicializado.");
                this.firebaseInitialized = false;
                return;
            }
            log.info("✅ Usando firebase-key.json en resources para desarrollo local...");
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                this.firebaseInitialized = true;
                log.info("🔥 Firebase inicializado exitosamente.");
            } catch (IOException e) {
                log.error("❌ Error inicializando Firebase: {}", e.getMessage());
                this.firebaseInitialized = false;
            }
        } else {
            this.firebaseInitialized = true;
            log.info("ℹ️ Firebase ya estaba inicializado.");
        }
    }

    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            log.warn("⚠️ Firebase no inicializado. Retornando null (solo afecta a desarrollo local).");
            return null;
        }

        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            log.error("❌ Error obteniendo Firestore client: {}", e.getMessage());
            return null;
        }
    }
}
