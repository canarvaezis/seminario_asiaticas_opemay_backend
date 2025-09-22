package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import co.edu.uniajc.estudiante.opemayfruitshop.model.CartItem;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class CartRepository {

    private static final String USERS = "users";
    private static final String CART = "cart";

    private CollectionReference getCartCollection(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(USERS)
                 .document(userId)
                 .collection(CART); // ✅ ya no hay .document("cart").collection("items")
    }

    public List<CartItem> getItems(String userId) throws ExecutionException, InterruptedException {
        List<CartItem> items = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = getCartCollection(userId).get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            CartItem item = doc.toObject(CartItem.class);
            item.setId(doc.getId()); // para saber el itemId en caso de eliminar
            items.add(item);
        }
        return items;
    }

    public CartItem addItem(String userId, CartItem item) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCartCollection(userId).document(); // nuevo doc en la subcolección
        item.setId(docRef.getId());
        docRef.set(item).get();
        return item;
    }

    public void removeItem(String userId, String itemId) throws ExecutionException, InterruptedException {
        getCartCollection(userId).document(itemId).delete().get();
    }

    public void clearCart(String userId) throws ExecutionException, InterruptedException {
        List<CartItem> items = getItems(userId);
        for (CartItem item : items) {
            getCartCollection(userId).document(item.getId()).delete().get();
        }
    }
}
