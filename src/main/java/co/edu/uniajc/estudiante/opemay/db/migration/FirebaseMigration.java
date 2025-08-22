package co.edu.uniajc.estudiante.opemay.db.migration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Component
public class FirebaseMigration implements CommandLineRunner {

    @Override
    public void run(String... args) {
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json")) {

            if (serviceAccount == null) {
                System.err.println("No se encontró el archivo firebase-key.json");
                return;
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }

            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> product = new HashMap<>();
            product.put("name", "Arroz");
            product.put("price", 2500);
            product.put("stock", 100);

            db.collection("products").add(product).get();

            System.out.println("Producto creado correctamente");

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
    }
}
