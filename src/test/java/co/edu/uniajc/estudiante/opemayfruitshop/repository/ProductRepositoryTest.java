package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

class ProductRepositoryTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collection;

    @Mock
    private DocumentReference document;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFuture;

    @InjectMocks
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new ProductRepository(firestore);
        when(firestore.collection("products")).thenReturn(collection);
        when(collection.document(anyString())).thenReturn(document);
    }

    @Test
    void testSaveProduct() throws Exception {
        Product product = new Product("1", "Manzana", 1.5, 100);
        when(document.set(product)).thenReturn(writeResultFuture);

        Product result = repository.save(product);
        assertNotNull(result);
        assertEquals("Manzana", result.getName());
        verify(document).set(product);
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(collection.get()).thenReturn(querySnapshotFuture);
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        List<Product> products = repository.findAll();
        assertNotNull(products);
        assertEquals(0, products.size());
    }

    @Test
    void testFindById() throws Exception {
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(collection.document("1")).thenReturn(document);
        when(document.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Product.class)).thenReturn(new Product("1", "Pera", 2.0, 50));

        Product product = repository.findById("1").orElseThrow();
        assertNotNull(product);
        assertEquals("Pera", product.getName());
    }

    @Test
    void testDeleteProduct() throws Exception {
        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        when(document.delete()).thenReturn(deleteFuture);
        repository.delete("1");
        verify(document).delete();
    }
}
