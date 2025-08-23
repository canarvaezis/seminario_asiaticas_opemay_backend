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
            throw new IOException("No se encontrÃ³ firebase-key.json en el classpath. Coloca el archivo en src/main/resources.");
        }
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://(default).firebaseio.com/")
                .build();

        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
