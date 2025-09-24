package co.edu.uniajc.estudiante.opemay.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseInitializer {

    @PostConstruct
    public void initFirestore() throws IOException {
        // 🔑 Trae el secret como string (se inyecta en CI desde GitHub Actions)
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_01");

        InputStream serviceAccount;
        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        } else {
            // fallback: archivo local en resources para desarrollo
            serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");
        }

        if (serviceAccount == null) {
            System.out.println("⚠️ No se encontró configuración de Firebase. Saltando inicialización...");
            return;
        }

        // ✅ Evita IllegalStateException en tests o múltiples contextos
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado correctamente");
        } else {
            System.out.println("ℹ️ Firebase ya estaba inicializado, se omite");
        }
    }

    @Bean
    public Firestore firestore() {
        // 🔥 Lanza error solo si de verdad Firebase no está inicializado
        return FirestoreClient.getFirestore();
    }
}
