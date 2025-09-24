package co.edu.uniajc.estudiante.opemay.config;
//HOLA
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseInitializer {

    @PostConstruct
    public void iniFirestore() throws IOException {
        String firebaseConfig = System.getenv("FIREBASE_CONFIG_01");

        InputStream serviceAccount;
        if (firebaseConfig != null && !firebaseConfig.isBlank()) {
            // 👇 Convertimos la variable de entorno en un InputStream
            serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        } else {
            // ⚠️ Si no hay variable, intenta leer el archivo local (para desarrollo)
            serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");
        }

        if (serviceAccount == null) {
            System.out.println("⚠️ No se encontró configuración de Firebase. Saltando inicialización...");
            return;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
