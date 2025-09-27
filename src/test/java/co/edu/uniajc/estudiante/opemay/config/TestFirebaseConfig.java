package co.edu.uniajc.estudiante.opemay.config;

import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;



/**
 * Configuración de Firebase para entorno de testing
 * Proporciona mocks de Firebase para evitar inicialización real
 * 
 * @author OpemAy Team
 * @version 1.0
 * @since 2025-09-26
 */
@TestConfiguration
@Profile("test")
public class TestFirebaseConfig {

    /**
     * Mock de FirebaseApp para testing
     */
    @Bean
    @Primary
    public FirebaseApp mockFirebaseApp() {
        FirebaseApp mockApp = Mockito.mock(FirebaseApp.class);
        when(mockApp.getName()).thenReturn("test-app");
        return mockApp;
    }

    /**
     * Mock de Firestore para testing
     */
    @Bean
    @Primary
    public Firestore mockFirestore() {
        Firestore mockFirestore = Mockito.mock(Firestore.class);
        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        DocumentReference mockDocument = Mockito.mock(DocumentReference.class);
        
        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);
        
        return mockFirestore;
    }


}