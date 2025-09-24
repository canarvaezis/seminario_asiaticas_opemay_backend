package co.edu.uniajc.estudiante.opemay.config;

import java.io.IOException;
import java.io.InputStream;

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
    InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");
    if (serviceAccount == null) {
        System.out.println("⚠️ firebase-key.json no encontrado. Saltando inicialización de Firebase...");
        return; // 👈 evita romper los tests
    }

    FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://(default).firebaseio.com/")
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
