package co.edu.uniajc.estudiante.opemay.respository;

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

import co.edu.uniajc.estudiante.opemay.model.Product;

@Repository
public class ProductRepository {

    private final Firestore firestore;

    public ProductRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    // Crear producto
    public Product save(Product product) throws ExecutionException, InterruptedException {
        String id = (product.getId() != null)
            ? product.getId()
            : firestore.collection("products").document().getId();

        product.setId(id);

        ApiFuture<WriteResult> future = firestore.collection("products")
                .document(id)
                .set(product);

        future.get(); // bloquea hasta que se guarde
        return product;
    }

    // Obtener todos los productos
    public List<Product> findAll() throws ExecutionException, InterruptedException {
        List<Product> products = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = firestore.collection("products").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot doc : documents) {
            products.add(doc.toObject(Product.class));
        }
        return products;
    }

    // Obtener por ID
    public Product findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("products").document(id);
        DocumentSnapshot document = docRef.get().get();
        if (document.exists()) {
            return document.toObject(Product.class);
        } else {
            return null;
        }
    }

    // Actualizar producto
    public Product update(String id, Product updatedProduct) throws ExecutionException, InterruptedException {
        updatedProduct.setId(id);
        firestore.collection("products").document(id).set(updatedProduct).get();
        return updatedProduct;
    }

    // Eliminar producto
    public void delete(String id) throws ExecutionException, InterruptedException {
        firestore.collection("products").document(id).delete().get();
    }
}
