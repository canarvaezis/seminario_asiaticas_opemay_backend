package co.edu.uniajc.estudiante.opemay.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemay.model.Product;

/**
 * Tests unitarios para ProductService
 */
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
    private ApiFuture<WriteResult> writeResultFuture;
    
    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;
    
    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;
    
    @Mock
    private WriteResult writeResult;
    
    @Mock
    private QuerySnapshot querySnapshot;
    
    @Mock
    private DocumentSnapshot documentSnapshot;

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
                .active(true)
                .category("Electronics")
                .stock(10)
                .imageUrl("http://test.com/image.jpg")
                .createdAt(Timestamp.now())
                .build();
    }

    @Nested
    @DisplayName("createProduct Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Debe crear un producto exitosamente")
        void shouldCreateProductSuccessfully() throws Exception {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(anyString())).thenReturn(documentReference);
            when(documentReference.getId()).thenReturn("generated-id");
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);
            when(writeResultFuture.get()).thenReturn(writeResult);
            when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

            // Act
            Product result = productService.createProduct(testProduct);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Test Product");
            assertThat(result.getPrice()).isEqualTo(99.99);
            verify(firestore).collection("products");
            verify(documentReference).set(any(Product.class));
        }

        @Test
        @DisplayName("Debe generar ID automáticamente si no existe")
        void shouldGenerateIdWhenNotProvided() throws Exception {
            // Arrange
            Product productWithoutId = Product.builder()
                    .name("Test Product")
                    .price(99.99)
                    .build();
            
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document()).thenReturn(documentReference);
            when(documentReference.getId()).thenReturn("auto-generated-id");
            when(collectionReference.document("auto-generated-id")).thenReturn(documentReference);
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);
            when(writeResultFuture.get()).thenReturn(writeResult);
            when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

            // Act
            Product result = productService.createProduct(productWithoutId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("auto-generated-id");
        }

        @Test
        @DisplayName("Debe manejar excepción y usar RuntimeException")
        void shouldHandleExceptionWithRuntimeException() throws Exception {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(anyString())).thenReturn(documentReference);
            when(documentReference.set(any(Product.class))).thenReturn(writeResultFuture);
            when(writeResultFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> productService.createProduct(testProduct));
        }
    }

    @Nested
    @DisplayName("getAllProducts Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Debe obtener todos los productos exitosamente")
        void shouldGetAllProductsSuccessfully() throws Exception {
            // Arrange
            QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
            QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
            
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));
            
            // Configurar mocks de documentos
            when(doc1.getId()).thenReturn("1");
            when(doc1.getString("name")).thenReturn("Product 1");
            when(doc1.getDouble("price")).thenReturn(10.0);
            when(doc1.getString("description")).thenReturn("Description 1");
            when(doc1.getBoolean("active")).thenReturn(true);
            
            when(doc2.getId()).thenReturn("2");
            when(doc2.getString("name")).thenReturn("Product 2");
            when(doc2.getDouble("price")).thenReturn(20.0);
            when(doc2.getString("description")).thenReturn("Description 2");
            when(doc2.getBoolean("active")).thenReturn(true);

            // Act
            List<Product> result = productService.getAllProducts();

            // Assert
            assertThat(result).isNotNull();
            verify(firestore).collection("products");
            verify(collectionReference).get();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay productos")
        void shouldReturnEmptyListWhenNoProducts() throws Exception {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenReturn(querySnapshot);
            when(querySnapshot.getDocuments()).thenReturn(Arrays.asList());

            // Act
            List<Product> result = productService.getAllProducts();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Debe ejecutar fallback en caso de error")
        void shouldExecuteFallbackOnError() throws Exception {
            // Arrange
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.get()).thenReturn(querySnapshotFuture);
            when(querySnapshotFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            List<Product> result = productService.getAllProducts();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("fallback-id");
            assertThat(result.get(0).getName()).isEqualTo("Productos no disponibles");
        }
    }

    @Nested
    @DisplayName("getProductById Tests")
    class GetProductByIdTests {

        @Test
        @DisplayName("Debe obtener producto por ID exitosamente")
        void shouldGetProductByIdSuccessfully() throws Exception {
            // Arrange
            String productId = "test-id";
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(productId)).thenReturn(documentReference);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            
            // Configurar mock del documento
            when(documentSnapshot.getId()).thenReturn(productId);
            when(documentSnapshot.getString("name")).thenReturn("Test Product");
            when(documentSnapshot.getDouble("price")).thenReturn(99.99);
            when(documentSnapshot.getString("description")).thenReturn("Test Description");
            when(documentSnapshot.getBoolean("active")).thenReturn(true);

            // Act
            Product result = productService.getProductById(productId);

            // Assert
            assertThat(result).isNotNull();
            verify(firestore).collection("products");
            verify(collectionReference).document(productId);
        }

        @Test
        @DisplayName("Debe retornar null cuando producto no existe")
        void shouldReturnNullWhenProductNotExists() throws Exception {
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
        @DisplayName("Debe ejecutar fallback en caso de error")
        void shouldExecuteFallbackOnError() throws Exception {
            // Arrange
            String productId = "test-id";
            when(firestore.collection("products")).thenReturn(collectionReference);
            when(collectionReference.document(productId)).thenReturn(documentReference);
            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException()));

            // Act
            Product result = productService.getProductById(productId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(productId);
            assertThat(result.getName()).isEqualTo("Producto no disponible");
            assertThat(result.getActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Fallback Methods Tests")
    class FallbackMethodsTests {

        @Test
        @DisplayName("createProductFallback debe retornar producto por defecto")
        void createProductFallbackShouldReturnDefaultProduct() {
            // Arrange
            Product inputProduct = Product.builder().name("Test").build();
            Exception exception = new RuntimeException("Test error");

            // Act
            Product result = productService.createProductFallback(inputProduct, exception);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("fallback-id");
            assertThat(result.getName()).isEqualTo("Producto no disponible temporalmente");
            assertThat(result.getPrice()).isEqualTo(0.0);
            assertThat(result.getActive()).isFalse();
        }

        @Test
        @DisplayName("getAllProductsFallback debe retornar lista con producto por defecto")
        void getAllProductsFallbackShouldReturnDefaultProductList() {
            // Arrange
            Exception exception = new RuntimeException("Test error");

            // Act
            List<Product> result = productService.getAllProductsFallback(exception);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("fallback-id");
            assertThat(result.get(0).getName()).isEqualTo("Productos no disponibles");
        }

        @Test
        @DisplayName("getProductByIdFallback debe retornar producto por defecto con ID especificado")
        void getProductByIdFallbackShouldReturnDefaultProductWithId() {
            // Arrange
            String productId = "test-id";
            Exception exception = new RuntimeException("Test error");

            // Act
            Product result = productService.getProductByIdFallback(productId, exception);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(productId);
            assertThat(result.getName()).isEqualTo("Producto no disponible");
            assertThat(result.getPrice()).isEqualTo(0.0);
            assertThat(result.getActive()).isFalse();
        }
    }
}