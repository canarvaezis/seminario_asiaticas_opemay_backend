package co.edu.uniajc.estudiante.opemay.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemay.model.Product;

@Service
public class ProductService {

    private final Firestore firestore;

    public ProductService(Firestore firestore) {
        this.firestore = firestore;
    }

    public Product createProduct(Product product) {
        try {
            // Guardar en la colección "products"
            ApiFuture<WriteResult> future = firestore.collection("products")
                    .document(product.getId() != null ? product.getId() : firestore.collection("products").document().getId())
                    .set(product);

            System.out.println("Product saved at: " + future.get().getUpdateTime());
            return product;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving product in Firebase", e);
        }
    }

        public List<Product> getAllProducts() {
            List<Product> products = new ArrayList<>();
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection("products").get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot doc : documents) {
                    Product product = doc.toObject(Product.class);
                    products.add(product);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error retrieving products from Firebase", e);
            }
            return products;
        }
}
