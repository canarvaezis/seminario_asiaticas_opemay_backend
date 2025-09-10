package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

class ProductRepositoryTest {

    private Firestore firestore;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        firestore = mock(Firestore.class);
        collectionReference = mock(CollectionReference.class);
        documentReference = mock(DocumentReference.class);

        when(firestore.collection("products")).thenReturn(collectionReference);

        repository = new ProductRepository(firestore);
    }

    // 🔹 Método auxiliar para simular ApiFuture correctamente
    private <T> ApiFuture<T> mockFuture(T value) {
        return ApiFutures.immediateFuture(value);
    }

    @Test
    void testFindAll() throws Exception {
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class); // 👈 tipo correcto

        Product product = new Product("1", "Apple", 100, 2.5);

        when(collectionReference.get()).thenReturn(mockFuture(querySnapshot));
        when(querySnapshot.getDocuments()).thenReturn(List.of(documentSnapshot));
        when(documentSnapshot.toObject(Product.class)).thenReturn(product);

        List<Product> result = repository.findAll();

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getName());
    }

    @Test
    void testFindByIdExists() throws Exception {
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        Product product = new Product("1", "Apple", 100, 2.5);

        when(collectionReference.document("1")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(mockFuture(snapshot));
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Product.class)).thenReturn(product);

        Optional<Product> result = repository.findById("1");

        assertTrue(result.isPresent());
        assertEquals("Apple", result.get().getName());
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(collectionReference.document("1")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(mockFuture(snapshot));
        when(snapshot.exists()).thenReturn(false);

        Optional<Product> result = repository.findById("1");

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveWithId() throws Exception {
        Product product = new Product("1", "Banana", 50, 1.5);

        when(collectionReference.document("1")).thenReturn(documentReference);
        when(documentReference.set(product)).thenReturn(mockFuture(mock(WriteResult.class)));

        Product saved = repository.save(product);

        assertEquals("1", saved.getId());
        assertEquals("Banana", saved.getName());
    }

    @Test
    void testSaveWithoutId() throws Exception {
        Product product = new Product(null, "Orange", 30, 0.8);

        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generated-id");
        when(documentReference.set(product)).thenReturn(mockFuture(mock(WriteResult.class)));

        Product saved = repository.save(product);

        assertNotNull(saved.getId());
        assertEquals("generated-id", saved.getId());
    }

    @Test
    void testDelete() throws Exception {
        when(collectionReference.document("1")).thenReturn(documentReference);
        when(documentReference.delete()).thenReturn(mockFuture(mock(WriteResult.class)));

        assertDoesNotThrow(() -> repository.delete("1"));
    }
}
