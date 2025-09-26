package co.edu.uniajc.estudiante.opemay.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FirebaseInitializerTest {

    private FirebaseInitializer firebaseInitializer;

    @BeforeEach
    void setUp() {
        firebaseInitializer = new FirebaseInitializer();
    }

    @Test
    void testInitializeFirebase_Success() throws IOException {
        // Arrange
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleCredentials> googleCredentialsMock = mockStatic(GoogleCredentials.class)) {
            
            GoogleCredentials mockCredentials = mock(GoogleCredentials.class);
            FirebaseApp mockFirebaseApp = mock(FirebaseApp.class);
            
            googleCredentialsMock.when(GoogleCredentials::getApplicationDefault)
                    .thenReturn(mockCredentials);
            firebaseAppMock.when(() -> FirebaseApp.getApps()).thenReturn(java.util.Collections.emptyList());
            firebaseAppMock.when(() -> FirebaseApp.initializeApp(any())).thenReturn(mockFirebaseApp);
            
            // Act & Assert - No debería lanzar excepción
            assertDoesNotThrow(() -> firebaseInitializer.initializeFirebase());
        }
    }

    @Test
    void testInitializeFirebase_AlreadyInitialized() {
        // Arrange
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            FirebaseApp existingApp = mock(FirebaseApp.class);
            firebaseAppMock.when(() -> FirebaseApp.getApps())
                    .thenReturn(java.util.Arrays.asList(existingApp));
            
            // Act & Assert - No debería lanzar excepción cuando ya está inicializado
            assertDoesNotThrow(() -> firebaseInitializer.initializeFirebase());
            
            // Verificar que no se llama initializeApp cuando ya existe
            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(any()), never());
        }
    }

    @Test
    void testInitializeFirebase_IOException() throws IOException {
        // Arrange
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class);
             MockedStatic<GoogleCredentials> googleCredentialsMock = mockStatic(GoogleCredentials.class)) {
            
            googleCredentialsMock.when(GoogleCredentials::getApplicationDefault)
                    .thenThrow(new IOException("Credentials not found"));
            firebaseAppMock.when(() -> FirebaseApp.getApps()).thenReturn(java.util.Collections.emptyList());
            
            // Act & Assert
            assertThrows(RuntimeException.class, () -> firebaseInitializer.initializeFirebase());
        }
    }

    @Test
    void testInitializeFirebase_RuntimeException() {
        // Arrange
        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(() -> FirebaseApp.getApps())
                    .thenThrow(new RuntimeException("Firebase initialization failed"));
            
            // Act & Assert
            assertThrows(RuntimeException.class, () -> firebaseInitializer.initializeFirebase());
        }
    }

    @Test
    void testPostConstruct() {
        // Este test verifica que el método anotado con @PostConstruct existe y es llamable
        assertNotNull(firebaseInitializer);
        
        // Verificar que el método existe usando reflexión
        boolean hasPostConstructMethod = false;
        try {
            java.lang.reflect.Method method = FirebaseInitializer.class.getDeclaredMethod("initializeFirebase");
            hasPostConstructMethod = method.isAnnotationPresent(jakarta.annotation.PostConstruct.class);
        } catch (NoSuchMethodException e) {
            fail("El método initializeFirebase debería existir");
        }
        
        assertTrue(hasPostConstructMethod, "El método debería estar anotado con @PostConstruct");
    }

    @Test
    void testFirebaseInitializerInstantiation() {
        // Test que la clase puede ser instanciada correctamente
        assertNotNull(firebaseInitializer);
        assertTrue(firebaseInitializer instanceof FirebaseInitializer);
    }

    @Test
    void testClassAnnotations() {
        // Verificar que la clase tiene las anotaciones correctas
        assertTrue(firebaseInitializer.getClass().isAnnotationPresent(org.springframework.stereotype.Configuration.class),
                "La clase debería estar anotada con @Configuration");
    }
}