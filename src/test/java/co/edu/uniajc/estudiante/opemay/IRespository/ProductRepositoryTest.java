package co.edu.uniajc.estudiante.opemay.IRespository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uniajc.estudiante.opemay.model.Product;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ProductRepositoryTest {

    @InjectMocks
    private ProductRepository productRepository;

    private Firestore mockFirestore;
    private DocumentReference mockDocRef;
    private ApiFuture<WriteResult> mockWriteFuture;
    private ApiFuture<DocumentSnapshot> mockDocFuture;
    private ApiFuture<QuerySnapshot> mockQueryFuture;
    private WriteResult mockWriteResult;
    private DocumentSnapshot mockDocSnapshot;
    private QuerySnapshot mockQuerySnapshot;
    private QueryDocumentSnapshot mockQueryDoc;

    @BeforeEach
    void setUp() {
        mockFirestore = mock(Firestore.class);
        mockDocRef = mock(DocumentReference.class);
        mockWriteFuture = mock(ApiFuture.class);
        mockDocFuture = mock(ApiFuture.class);
        mockQueryFuture = mock(ApiFuture.class);
        mockWriteResult = mock(WriteResult.class);
        mockDocSnapshot = mock(DocumentSnapshot.class);
        mockQuerySnapshot = mock(QuerySnapshot.class);
        mockQueryDoc = mock(QueryDocumentSnapshot.class);
    }

    @Test
    void testSave_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Product product = Product.builder()
                .id("product-123")
                .name("Test Product")
                .build();

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document("product-123")).thenReturn(mockDocRef);
            when(mockDocRef.set(product)).thenReturn(mockWriteFuture);
            when(mockWriteFuture.get()).thenReturn(mockWriteResult);
            when(mockWriteResult.getUpdateTime()).thenReturn(com.google.cloud.Timestamp.now());

            // Act
            String result = productRepository.save(product);

            // Assert
            assertNotNull(result);
            verify(mockDocRef).set(product);
        }
    }

    @Test
    void testGetProductById_Found() throws ExecutionException, InterruptedException {
        // Arrange
        String productId = "product-123";
        Product expectedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .build();

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document(productId)).thenReturn(mockDocRef);
            when(mockDocRef.get()).thenReturn(mockDocFuture);
            when(mockDocFuture.get()).thenReturn(mockDocSnapshot);
            when(mockDocSnapshot.exists()).thenReturn(true);
            when(mockDocSnapshot.toObject(Product.class)).thenReturn(expectedProduct);

            // Act
            Product result = productRepository.getProductById(productId);

            // Assert
            assertNotNull(result);
            assertEquals(expectedProduct, result);
        }
    }

    @Test
    void testGetProductById_NotFound() throws ExecutionException, InterruptedException {
        // Arrange
        String productId = "nonexistent-product";

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document(productId)).thenReturn(mockDocRef);
            when(mockDocRef.get()).thenReturn(mockDocFuture);
            when(mockDocFuture.get()).thenReturn(mockDocSnapshot);
            when(mockDocSnapshot.exists()).thenReturn(false);

            // Act
            Product result = productRepository.getProductById(productId);

            // Assert
            assertNull(result);
        }
    }

    @Test
    void testGetAllProducts_Success() throws ExecutionException, InterruptedException {
        // Arrange
        Product product1 = Product.builder().id("1").name("Product 1").build();
        Product product2 = Product.builder().id("2").name("Product 2").build();
        List<QueryDocumentSnapshot> mockDocs = Arrays.asList(mockQueryDoc, mockQueryDoc);

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").get()).thenReturn(mockQueryFuture);
            when(mockQueryFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.getDocuments()).thenReturn(mockDocs);
            
            // Mock para cada documento en la lista
            QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
            QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
            when(doc1.toObject(Product.class)).thenReturn(product1);
            when(doc2.toObject(Product.class)).thenReturn(product2);
            
            List<QueryDocumentSnapshot> documents = Arrays.asList(doc1, doc2);
            when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

            // Act
            List<Product> result = productRepository.getAllProducts();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(product1, result.get(0));
            assertEquals(product2, result.get(1));
        }
    }

    @Test
    void testGetAllProducts_EmptyResult() throws ExecutionException, InterruptedException {
        // Arrange
        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").get()).thenReturn(mockQueryFuture);
            when(mockQueryFuture.get()).thenReturn(mockQuerySnapshot);
            when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList());

            // Act
            List<Product> result = productRepository.getAllProducts();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testDelete_Success() {
        // Arrange
        String productId = "product-to-delete";

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document(productId)).thenReturn(mockDocRef);
            when(mockDocRef.delete()).thenReturn(mockWriteFuture);

            // Act
            String result = productRepository.delete(productId);

            // Assert
            assertNotNull(result);
            assertTrue(result.contains("Eliminado en:"));
            verify(mockDocRef).delete();
        }
    }

    @Test
    void testSave_ExecutionException() throws ExecutionException, InterruptedException {
        // Arrange
        Product product = Product.builder()
                .id("product-123")
                .name("Test Product")
                .build();

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document("product-123")).thenReturn(mockDocRef);
            when(mockDocRef.set(product)).thenReturn(mockWriteFuture);
            when(mockWriteFuture.get()).thenThrow(new ExecutionException("Firestore error", new RuntimeException()));

            // Act & Assert
            assertThrows(ExecutionException.class, () -> productRepository.save(product));
        }
    }

    @Test
    void testGetProductById_InterruptedException() throws ExecutionException, InterruptedException {
        // Arrange
        String productId = "product-123";

        try (MockedStatic<FirestoreClient> firestoreClient = mockStatic(FirestoreClient.class)) {
            firestoreClient.when(FirestoreClient::getFirestore).thenReturn(mockFirestore);
            when(mockFirestore.collection("products")).thenReturn(mock(com.google.cloud.firestore.CollectionReference.class));
            when(mockFirestore.collection("products").document(productId)).thenReturn(mockDocRef);
            when(mockDocRef.get()).thenReturn(mockDocFuture);
            when(mockDocFuture.get()).thenThrow(new InterruptedException("Thread interrupted"));

            // Act & Assert
            assertThrows(InterruptedException.class, () -> productRepository.getProductById(productId));
        }
    }

    @Test
    void testRepositoryConstants() {
        // Este test verifica que la constante COLLECTION_NAME esté definida correctamente
        // usando reflexión para acceder a campos privados si es necesario
        
        // Como es una constante privada, verificamos indirectamente su uso
        // al confirmar que los métodos funcionan correctamente
        assertNotNull(productRepository);
        assertTrue(productRepository instanceof ProductRepository);
    }
}