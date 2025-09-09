package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

class ProductServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collection;

    @Mock
    private DocumentReference document;

    @Mock
    private ApiFuture<DocumentSnapshot> documentFuture;

    @Mock
    private ApiFuture<QuerySnapshot> queryFuture;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Aseguramos que firestore.collection devuelva collection
        when(firestore.collection("products")).thenReturn(collection);
    }

    @Test
    void testSaveProduct() throws ExecutionException, InterruptedException {
        Product product = new Product("1", "Apple", 1.5, 100);
        when(collection.document("1")).thenReturn(document);
        @SuppressWarnings("unchecked")
        ApiFuture<WriteResult> writeFuture = mock(ApiFuture.class);
        when(document.set(product)).thenReturn(writeFuture);

        Product result = productService.save(product);

        assertEquals(product.getId(), result.getId());
        verify(document, times(1)).set(product);
    }

    @Test
    void testFindById() throws ExecutionException, InterruptedException {
        Product product = new Product("1", "Apple", 1.5, 100);

        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Product.class)).thenReturn(product);
        when(documentFuture.get()).thenReturn(snapshot);

        when(collection.document("1")).thenReturn(document);
        when(document.get()).thenReturn(documentFuture);

        Optional<Product> result = productService.findById("1");

        assertTrue(result.isPresent());
        assertEquals("Apple", result.get().getName());
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        Product product1 = new Product("1", "Apple", 1.5, 100);
        Product product2 = new Product("2", "Banana", 0.5, 50);

        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(doc1.toObject(Product.class)).thenReturn(product1);
        when(doc2.toObject(Product.class)).thenReturn(product2);

        List<QueryDocumentSnapshot> docs = Arrays.asList(doc1, doc2);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(docs);

        when(queryFuture.get()).thenReturn(querySnapshot);
        when(collection.get()).thenReturn(queryFuture);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testDeleteProduct() throws ExecutionException, InterruptedException {
        when(collection.document("1")).thenReturn(document);
        @SuppressWarnings("unchecked")
        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        when(document.delete()).thenReturn(deleteFuture);

        productService.delete("1");

        verify(document, times(1)).delete();
    }
}
