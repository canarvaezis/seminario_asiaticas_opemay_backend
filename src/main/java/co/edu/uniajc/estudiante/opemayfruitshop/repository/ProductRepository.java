package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

@Repository
public class ProductRepository {

    private final String COLLECTION = "products";

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION).get();
        return query.get().getDocuments().stream()
                .map(doc -> doc.toObject(Product.class))
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION).document(id);
        DocumentSnapshot snapshot = docRef.get().get();
        if(snapshot.exists()) {
            return Optional.of(snapshot.toObject(Product.class));
        } else {
            return Optional.empty();
        }
    }

    public Product save(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        if(product.getId() == null || product.getId().isEmpty()) {
            DocumentReference docRef = db.collection(COLLECTION).document();
            product.setId(docRef.getId());
            docRef.set(product).get();
        } else {
            db.collection(COLLECTION).document(product.getId()).set(product).get();
        }
        return product;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION).document(id).delete().get();
    }
}
