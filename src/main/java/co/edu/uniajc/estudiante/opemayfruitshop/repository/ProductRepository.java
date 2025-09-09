package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

@Repository
public class ProductRepository {

    private static final String COLLECTION_NAME = "products";

    public Product save(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(product.getId());
        ApiFuture<WriteResult> future = docRef.set(product);
        future.get();
        return product;
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            products.add(doc.toObject(Product.class));
        }
        return products;
    }

    public Product findById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();
        return document.exists() ? document.toObject(Product.class) : null;
    }

    public String delete(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
        writeResult.get();
        return "Deleted product with ID: " + id;
    }
}
