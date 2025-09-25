package co.edu.uniajc.estudiante.opemay.config;

import com.google.cloud.firestore.Firestore;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuración específica de Firebase para tests
 * Esta configuración proporciona mocks de Firebase para evitar 
 * la necesidad de credenciales reales durante las pruebas
 */
@TestConfiguration
@Profile("test")
public class FirebaseTestConfig {

    /**
     * Proporciona un mock de Firestore para tests
     * Esto permite que los servicios que dependen de Firestore
     * funcionen en el entorno de pruebas sin configuración real
     */
    @Bean
    @Primary
    public Firestore mockFirestore() {
        Firestore mockFirestore = Mockito.mock(Firestore.class);
        
        // Podemos configurar comportamientos por defecto si es necesario
        // Por ejemplo:
        // when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        
        return mockFirestore;
    }
}