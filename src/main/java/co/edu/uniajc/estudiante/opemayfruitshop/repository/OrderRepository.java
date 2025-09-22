package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Order;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class OrderRepository {

    private static final String ORDERS = "orders";

    private CollectionReference getCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(ORDERS);
    }

    public Order save(Order order) throws ExecutionException, InterruptedException {
        getCollection().document(order.getId()).set(order).get();
        return order;
    }

    public Order findById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = getCollection().document(id).get().get();
        return doc.exists() ? doc.toObject(Order.class) : null;
    }

    public List<Order> findAll() throws ExecutionException, InterruptedException {
        List<Order> orders = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = getCollection().get();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            orders.add(doc.toObject(Order.class));
        }
        return orders;
    }
}
