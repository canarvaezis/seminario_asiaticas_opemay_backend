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

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

@Repository
public class ProductRepository {

    private final String COLLECTION = "products";
    private final Firestore db;

    // Inyección de Firestore (para tests)
    public ProductRepository(Firestore db) {
        this.db = db;
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION).get();
        return query.get().getDocuments().stream()
                .map(doc -> doc.toObject(Product.class))
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION).document(id);
        DocumentSnapshot snapshot = docRef.get().get();
        if(snapshot.exists()) {
            return Optional.of(snapshot.toObject(Product.class));
        } else {
            return Optional.empty();
        }
    }

    public Product save(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef;
        if(product.getId() == null || product.getId().isEmpty()) {
            docRef = db.collection(COLLECTION).document();
            product.setId(docRef.getId());
        } else {
            docRef = db.collection(COLLECTION).document(product.getId());
        }
        docRef.set(product).get();
        return product;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION).document(id).delete().get();
    }
}
