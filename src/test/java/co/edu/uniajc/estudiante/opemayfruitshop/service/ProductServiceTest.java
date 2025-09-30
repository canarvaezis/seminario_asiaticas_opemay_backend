package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private CollectionReference colRef;
    private DocumentReference docRef;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        firestore = mock(Firestore.class);
        colRef = mock(CollectionReference.class);
        docRef = mock(DocumentReference.class);

        // Simular acceso a la colección "products"
        when(firestore.collection("products")).thenReturn(colRef);
        when(colRef.document(anyString())).thenReturn(docRef);

        productService = new ProductService(firestore);
    }

    @Test
    void testSaveProduct() throws Exception {
        Product product = new Product("1", "Apple", 100, 2.5);

        ApiFuture<WriteResult> future = mock(ApiFuture.class);
        when(docRef.set(product)).thenReturn(future);
        when(future.get()).thenReturn(mock(WriteResult.class));

        Product saved = productService.save(product);

        assertEquals("Apple", saved.getName());
        verify(docRef, times(1)).set(product);
    }

    @Test
    void testFindByIdExists() throws Exception {
        Product product = new Product("1", "Banana", 50, 1.2);

        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);

        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Product.class)).thenReturn(product);

        Optional<Product> found = productService.findById("1");

        assertTrue(found.isPresent());
        assertEquals("Banana", found.get().getName());
    }

    @Test
    void testFindByIdNotExists() throws Exception {
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);

        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(false);

        Optional<Product> found = productService.findById("999");

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAll() throws Exception {
        Product product1 = new Product("1", "Apple", 100, 2.5);
        Product product2 = new Product("2", "Orange", 200, 3.0);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);

        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(colRef.get()).thenReturn(future);
        when(future.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));

        when(doc1.toObject(Product.class)).thenReturn(product1);
        when(doc2.toObject(Product.class)).thenReturn(product2);

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertEquals("Apple", products.get(0).getName());
        assertEquals("Orange", products.get(1).getName());
    }

    @Test
    void testDelete() throws Exception {
        ApiFuture<WriteResult> future = mock(ApiFuture.class);
        when(docRef.delete()).thenReturn(future);
        when(future.get()).thenReturn(mock(WriteResult.class));

        productService.delete("1");

        verify(docRef, times(1)).delete();
    }
}
