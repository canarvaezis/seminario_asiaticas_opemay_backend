package co.edu.uniajc.estudiante.opemay.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@ExtendWith(MockitoExtension.class)
class FirebaseInitializerTest {

    @Test
    void testInitFirestore_Success() {
        // Este test verifica que la clase existe y es configurable
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        assertNotNull(firebaseInitializer);
        
        // Verificar que la clase está marcada como Configuration
        assertTrue(firebaseInitializer.getClass().isAnnotationPresent(Configuration.class),
                "FirebaseInitializer debe estar marcada con @Configuration");
    }

    @Test
    void testInitFirestore_WithEnvironmentVariable() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            FirebaseApp mockFirebaseApp = mock(FirebaseApp.class);
            firebaseAppMock.when(() -> FirebaseApp.getApps()).thenReturn(java.util.Collections.emptyList());
            firebaseAppMock.when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class))).thenReturn(mockFirebaseApp);
            
            // Act & Assert - No debería lanzar excepción
            assertDoesNotThrow(() -> firebaseInitializer.initFirestore());
        }
    }

    @Test
    void testInitFirestore_AlreadyInitialized() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            FirebaseApp existingApp = mock(FirebaseApp.class);
            firebaseAppMock.when(() -> FirebaseApp.getApps())
                    .thenReturn(java.util.Arrays.asList(existingApp));
            
            // Act & Assert - No debería lanzar excepción cuando ya está inicializado
            assertDoesNotThrow(() -> firebaseInitializer.initFirestore());
            
            // Verificar que no se intenta inicializar de nuevo
            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), never());
        }
    }

    @Test
    void testInitFirestore_NoEnvironmentVariable() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        // Act & Assert - No debería lanzar excepción cuando no hay variable de entorno
        assertDoesNotThrow(() -> firebaseInitializer.initFirestore());
    }

    @Test
    void testInitFirestore_InitializationException() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(() -> FirebaseApp.getApps()).thenReturn(java.util.Collections.emptyList());
            firebaseAppMock.when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)))
                    .thenThrow(new RuntimeException("Initialization failed"));
            
            // Act & Assert - No debería lanzar excepción, pero maneja el error internamente
            assertDoesNotThrow(() -> firebaseInitializer.initFirestore());
        }
    }

    @Test
    void testFirestoreBean_WhenNotInitialized() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        // Act
        Firestore firestore = firebaseInitializer.firestore();
        
        // Assert - Debería devolver null cuando no está inicializado
        assertNull(firestore);
    }

    @Test
    void testFirestoreBean_InitializationState() {
        // Arrange
        FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
        
        // Act & Assert - El bean firestore no debería lanzar excepción
        assertDoesNotThrow(() -> firebaseInitializer.firestore());
    }

    @Test
    void testClassAnnotations() {
        // Verificar que la clase tiene las anotaciones correctas
        assertTrue(FirebaseInitializer.class.isAnnotationPresent(Configuration.class),
                "FirebaseInitializer debe estar marcada con @Configuration");
    }

    @Test
    void testBeanMethodExists() {
        // Verificar que el método firestore existe
        try {
            FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
            assertNotNull(firebaseInitializer.getClass().getDeclaredMethod("firestore"));
        } catch (NoSuchMethodException e) {
            fail("El método firestore() debe existir en FirebaseInitializer");
        }
    }

    @Test
    void testPostConstructMethodExists() {
        // Verificar que el método initFirestore existe
        try {
            FirebaseInitializer firebaseInitializer = new FirebaseInitializer();
            assertNotNull(firebaseInitializer.getClass().getDeclaredMethod("initFirestore"));
        } catch (NoSuchMethodException e) {
            fail("El método initFirestore() debe existir en FirebaseInitializer");
        }
    }
}