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
    
    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initFirestore() throws IOException {
        System.out.println("=== FIREBASE DEBUG INFO ===");

        // 🔑 Leer única variable de entorno
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_0X");
        System.out.println("FIREBASE_CONFIG_0X exists: " + (firebaseConfig != null));

        if (firebaseConfig == null || firebaseConfig.isBlank()) {
            System.out.println("❌ No se encontró FIREBASE_CONFIG_0X en variables de entorno. Saltando inicialización...");
            this.firebaseInitialized = false;
            return;
        }

        System.out.println("firebaseConfig length: " + firebaseConfig.length());
        System.out.println("firebaseConfig first 100 chars: " + 
            firebaseConfig.substring(0, Math.min(100, firebaseConfig.length())));

        InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

        // ✅ Evita IllegalStateException en tests o múltiples contextos
        System.out.println("Current FirebaseApp instances: " + FirebaseApp.getApps().size());
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado correctamente");
                this.firebaseInitialized = true;
            } catch (Exception e) {
                System.out.println("❌ Error inicializando Firebase: " + e.getMessage());
                e.printStackTrace();
                this.firebaseInitialized = false;
            }
        } else {
            System.out.println("ℹ Firebase ya estaba inicializado, se omite");
            this.firebaseInitialized = true;
        }
        
        System.out.println("=== END FIREBASE DEBUG ===");
    }

    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            System.out.println("⚠ Firebase no está inicializado. Retornando null para Firestore bean.");
            return null;
        }
        
        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            System.out.println("❌ Error obteniendo Firestore client: " + e.getMessage());
            return null;
        }
    }
}
