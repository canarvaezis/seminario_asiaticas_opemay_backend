package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;

@Service
public class ProductService {

    private final Firestore firestore;

    // Constructor inyectable para testing
    public ProductService(Firestore firestore) {
        this.firestore = firestore;
    }

    public Product save(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("products").document(product.getId());
        ApiFuture<WriteResult> future = docRef.set(product);
        future.get(); // Espera a que se guarde
        return product;
    }

    public Optional<Product> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("products").document(id);
        DocumentSnapshot snapshot = docRef.get().get();
        if (snapshot.exists()) {
            return Optional.of(snapshot.toObject(Product.class));
        }
        return Optional.empty();
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        CollectionReference colRef = firestore.collection("products");
        ApiFuture<QuerySnapshot> future = colRef.get();
        List<Product> products = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            products.add(doc.toObject(Product.class));
        }
        return products;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("products").document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }
}
