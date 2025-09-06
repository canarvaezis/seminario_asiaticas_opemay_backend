package co.edu.uniajc.estudiante.opemay.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.cloud.firestore.Firestore;

@TestConfiguration
public class FirestoreTestConfig {

    @Bean
    public Firestore firestore() {
        // Bean falso para evitar Firebase real en los tests
        return Mockito.mock(Firestore.class);
    }
}
