package co.edu.uniajc.estudiante.opemay.respository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import co.edu.uniajc.estudiante.opemay.model.Cart;

@Repository
public class CartRepository {

    private static final String COLLECTION = "carts";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public Cart getItem(String userId, String productId) throws ExecutionException, InterruptedException {
        DocumentReference ref = getFirestore()
                .collection(COLLECTION)
                .document(userId)
                .collection("items")
                .document(productId);
        ApiFuture<DocumentSnapshot> future = ref.get();
        DocumentSnapshot doc = future.get();
        if (doc.exists()) {
            Cart c = doc.toObject(Cart.class);
            // Asegura que si tu objeto Cart necesita el productId lo mantenga:
            if (c != null && (c.getProductId() == null || c.getProductId().isEmpty())) {
                c.setProductId(doc.getId());
            }
            return c;
        }
        return null;
    }

    public void saveItem(String userId, Cart item) throws ExecutionException, InterruptedException {
        getFirestore()
            .collection(COLLECTION)
            .document(userId)
            .collection("items")
            .document(item.getProductId())
            .set(item)
            .get(); // espera la confirmación
    }

    public void deleteItem(String userId, String productId) throws ExecutionException, InterruptedException {
        getFirestore()
            .collection(COLLECTION)
            .document(userId)
            .collection("items")
            .document(productId)
            .delete()
            .get();
    }

    public List<Cart> getItems(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getFirestore()
                .collection(COLLECTION)
                .document(userId)
                .collection("items")
                .get();

        List<Cart> result = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Cart c = doc.toObject(Cart.class);
            if (c != null) {
                if (c.getProductId() == null || c.getProductId().isEmpty()) c.setProductId(doc.getId());
                result.add(c);
            }
        }
        return result;
    }

    public double calculateTotal(String userId) throws ExecutionException, InterruptedException {
        List<Cart> items = getItems(userId);
        return items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
    }
}
