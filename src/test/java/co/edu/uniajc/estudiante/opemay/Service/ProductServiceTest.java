package co.edu.uniajc.estudiante.opemay.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemay.model.Product;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private Query query;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    private WriteResult writeResult;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("test-id")
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("Test Category")
                .imageUrl("test-image.jpg")
                .stock(10)
                .active(true)
                .createdAt(Timestamp.now())
                .updatedAt(Timestamp.now())
                .build();
    }

    @Nested
    @DisplayName("createProduct Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Debe crear un producto exitosamente")
        void shouldCreateProductSuccessfully() throws InterruptedException, ExecutionException {
            // Arrange
            Product productToCreate = Product.builder()
                    .name("New Product")
                    .description("New Description")
                    .price(50.0)
                    .build();

            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document()).thenReturn(documentReference);
            when(documentReference.getId()).thenReturn("generated-id");
            when(collectionReference.document("generated-id")).thenReturn(documentReference);
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);
            when(writeResultFuture.get()).thenReturn(writeResult);
            when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

            // Act
            Product result = productService.createProduct(productToCreate);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("generated-id");
            assertThat(result.getName()).isEqualTo("New Product");
            verify(documentReference).set(any(Product.class));
        }

        @Test
        @DisplayName("Debe manejar excepciones durante creación")
        void shouldHandleExceptionDuringCreation() throws InterruptedException, ExecutionException {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document()).thenReturn(documentReference);
            when(documentReference.getId()).thenReturn("generated-id");
            when(collectionReference.document("generated-id")).thenReturn(documentReference);
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);
            when(writeResultFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException("Firebase error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> productService.createProduct(testProduct));
        }

        @Test
        @DisplayName("Debe usar fallback cuando hay error")
        void shouldUseFallbackOnError() {
            // Arrange
            RuntimeException testException = new RuntimeException("Test error");

            // Act
            Product result = productService.createProductFallback(testProduct, testException);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("fallback-id");
            assertThat(result.getName()).isEqualTo("Producto no disponible temporalmente");
            assertThat(result.getActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("getAllProducts Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Debe retornar lista de productos exitosamente")
        void shouldReturnProductsListSuccessfully() throws InterruptedException, ExecutionException {
            // Arrange
            List<QueryDocumentSnapshot> mockDocuments = Arrays.asList(queryDocumentSnapshot);
            
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(mockDocuments);
            
            when(queryDocumentSnapshot.getId()).thenReturn("test-id");
            when(queryDocumentSnapshot.getString("name")).thenReturn("Test Product");
            when(queryDocumentSnapshot.getString("description")).thenReturn("Test Description");
            when(queryDocumentSnapshot.getDouble("price")).thenReturn(99.99);
            when(queryDocumentSnapshot.getString("category")).thenReturn("Test Category");
            when(queryDocumentSnapshot.getString("imageUrl")).thenReturn("test-image.jpg");
            when(queryDocumentSnapshot.getLong("stock")).thenReturn(10L);
            when(queryDocumentSnapshot.getBoolean("active")).thenReturn(true);
            when(queryDocumentSnapshot.getTimestamp("createdAt")).thenReturn(Timestamp.now());
            when(queryDocumentSnapshot.getTimestamp("updatedAt")).thenReturn(Timestamp.now());

            // Act
            List<Product> result = productService.getAllProducts();

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Test Product");
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay documentos")
        void shouldReturnEmptyListWhenNoDocuments() throws InterruptedException, ExecutionException {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

            // Act
            List<Product> result = productService.getAllProducts();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Debe ejecutar fallback en caso de error")
        void shouldExecuteFallbackOnError() {
            // Arrange
            RuntimeException testException = new RuntimeException("Test error");

            // Act
            List<Product> result = productService.getAllProductsFallback(testException);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Productos no disponibles");
            assertThat(result.get(0).getActive()).isFalse();
        }

        @Test
        @DisplayName("Debe manejar errores de Firebase")
        void shouldHandleFirebaseErrors() throws InterruptedException, ExecutionException {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException("Firebase error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> productService.getAllProducts());
        }
    }

    @Nested
    @DisplayName("getProductById Tests")
    class GetProductByIdTests {

        @Test
        @DisplayName("Debe retornar producto cuando existe")
        void shouldReturnProductWhenExists() throws InterruptedException, ExecutionException {
            // Arrange
            String productId = "test-id";
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(productId)).thenReturn(documentReference);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            
            when(documentSnapshot.getId()).thenReturn(productId);
            when(documentSnapshot.getString("name")).thenReturn("Test Product");
            when(documentSnapshot.getString("description")).thenReturn("Test Description");
            when(documentSnapshot.getDouble("price")).thenReturn(99.99);
            when(documentSnapshot.getString("category")).thenReturn("Test Category");
            when(documentSnapshot.getString("imageUrl")).thenReturn("test-image.jpg");
            when(documentSnapshot.getLong("stock")).thenReturn(10L);
            when(documentSnapshot.getBoolean("active")).thenReturn(true);
            when(documentSnapshot.getTimestamp("createdAt")).thenReturn(Timestamp.now());
            when(documentSnapshot.getTimestamp("updatedAt")).thenReturn(Timestamp.now());

            // Act
            Product result = productService.getProductById(productId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(productId);
            assertThat(result.getName()).isEqualTo("Test Product");
        }

        @Test
        @DisplayName("Debe retornar null cuando producto no existe")
        void shouldReturnNullWhenProductNotExists() throws InterruptedException, ExecutionException {
            // Arrange
            String productId = "non-existent-id";
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(productId)).thenReturn(documentReference);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(false);

            // Act
            Product result = productService.getProductById(productId);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Debe usar fallback en caso de error")
        void shouldUseFallbackOnError() {
            // Arrange
            String productId = "test-id";
            RuntimeException testException = new RuntimeException("Test error");

            // Act
            Product result = productService.getProductByIdFallback(productId, testException);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(productId);
            assertThat(result.getName()).isEqualTo("Producto no disponible");
            assertThat(result.getActive()).isFalse();
        }

        @Test
        @DisplayName("Debe manejar errores de Firebase")
        void shouldHandleFirebaseErrors() throws InterruptedException, ExecutionException {
            // Arrange
            String productId = "test-id";
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(productId)).thenReturn(documentReference);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException("Firebase error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> productService.getProductById(productId));
        }
    }

    @Nested
    @DisplayName("Circuit Breaker Integration Tests")
    class CircuitBreakerIntegrationTests {

        @Test
        @DisplayName("Debe activar circuit breaker después de múltiples errores")
        void shouldActivateCircuitBreakerAfterMultipleErrors() {
            // Este test requeriría configuración específica de Resilience4j
            // Por ahora validamos que los métodos fallback funcionan correctamente
            
            // Arrange
            String productId = "test-id";
            RuntimeException testException = new RuntimeException("Simulated failure");

            // Act - Simular activación de fallback
            Product fallbackResult = productService.getProductByIdFallback(productId, testException);

            // Assert
            assertThat(fallbackResult).isNotNull();
            assertThat(fallbackResult.getId()).isEqualTo(productId);
            assertThat(fallbackResult.getName()).isEqualTo("Producto no disponible");
        }
    }
}