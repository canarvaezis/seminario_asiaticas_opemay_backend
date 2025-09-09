package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    private Firestore firestore;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        firestore = mock(Firestore.class);
        productService = new ProductService(firestore);
    }

    @Test
    void testSaveProduct() throws ExecutionException, InterruptedException {
        Product product = new Product("1", "Manzana", 1.5, 100);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        CollectionReference colRef = mock(CollectionReference.class);

        when(firestore.collection("products")).thenReturn(colRef);
        when(colRef.document(product.getId())).thenReturn(docRef);
        when(docRef.set(product)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        Product saved = productService.save(product);
        assertEquals("Manzana", saved.getName());
    }

    @Test
    void testFindByIdExists() throws ExecutionException, InterruptedException {
        String id = "1";
        Product product = new Product(id, "Pera", 2.0, 50);

        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("products").document(id)).thenReturn(docRef);
        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Product.class)).thenReturn(product);

        Optional<Product> result = productService.findById(id);
        assertTrue(result.isPresent());
        assertEquals("Pera", result.get().getName());
    }

    @Test
    void testFindByIdNotExists() throws ExecutionException, InterruptedException {
        String id = "2";

        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("products").document(id)).thenReturn(docRef);
        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(false);

        Optional<Product> result = productService.findById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() throws ExecutionException, InterruptedException {
        Product product1 = new Product("1", "Manzana", 1.5, 100);
        Product product2 = new Product("2", "Pera", 2.0, 50);

        CollectionReference colRef = mock(CollectionReference.class);
        ApiFuture<QuerySnapshot> queryFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("products")).thenReturn(colRef);
        when(colRef.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));
        when(doc1.toObject(Product.class)).thenReturn(product1);
        when(doc2.toObject(Product.class)).thenReturn(product2);

        List<Product> products = productService.findAll();
        assertEquals(2, products.size());
        assertEquals("Manzana", products.get(0).getName());
        assertEquals("Pera", products.get(1).getName());
    }

    @Test
    void testDelete() throws ExecutionException, InterruptedException {
        String id = "1";
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        CollectionReference colRef = mock(CollectionReference.class);

        when(firestore.collection("products")).thenReturn(colRef);
        when(colRef.document(id)).thenReturn(docRef);
        when(docRef.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        productService.delete(id); // no exception = ok
    }
}
