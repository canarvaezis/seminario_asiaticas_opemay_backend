package co.edu.uniajc.estudiante.opemay.IRespository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import co.edu.uniajc.estudiante.opemay.model.Order;
import co.edu.uniajc.estudiante.opemay.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OrderRepository {

    private static final String COLLECTION_NAME = "orders";

    /**
     * Guarda una orden en Firestore
     */
    public String save(Order order) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(order.getId())
                .set(order);
        
        log.info("Orden guardada con ID: {}", order.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Obtiene una orden por su ID
     */
    public Order getOrderById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            log.info("Orden encontrada con ID: {}", id);
            return document.toObject(Order.class);
        }
        
        log.warn("Orden no encontrada con ID: {}", id);
        return null;
    }

    /**
     * Obtiene todas las órdenes de un usuario
     */
    public List<Order> getOrdersByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Order> orderList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            orderList.add(doc.toObject(Order.class));
        }

        log.info("Encontradas {} órdenes para usuario: {}", orderList.size(), userId);
        return orderList;
    }

    /**
     * Obtiene órdenes por estado
     */
    public List<Order> getOrdersByStatus(String status) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Order> orderList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            orderList.add(doc.toObject(Order.class));
        }

        log.info("Encontradas {} órdenes con estado: {}", orderList.size(), status);
        return orderList;
    }

    /**
     * Obtiene todas las órdenes (para administradores)
     */
    public List<Order> getAllOrders() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Order> orderList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            orderList.add(doc.toObject(Order.class));
        }

        log.info("Encontradas {} órdenes en total", orderList.size());
        return orderList;
    }

    /**
     * Obtiene órdenes recientes (últimos N días)
     */
    public List<Order> getRecentOrders(int days) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        
        // Calcular fecha límite
        long millisecondsAgo = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
        com.google.cloud.Timestamp limitDate = com.google.cloud.Timestamp.ofTimeMicroseconds(millisecondsAgo * 1000);
        
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereGreaterThan("createdAt", limitDate)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Order> orderList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            orderList.add(doc.toObject(Order.class));
        }

        log.info("Encontradas {} órdenes de los últimos {} días", orderList.size(), days);
        return orderList;
    }

    /**
     * Actualiza una orden existente
     */
    public String update(Order order) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(order.getId())
                .set(order);
        
        log.info("Orden actualizada con ID: {}", order.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Elimina una orden (soft delete)
     */
    public String softDelete(String id) throws ExecutionException, InterruptedException {
        Order order = getOrderById(id);
        if (order != null) {
            order.setActive(false);
            order.setStatus(OrderStatus.CANCELLED.name());
            return update(order);
        }
        
        log.warn("Intento de eliminar orden inexistente con ID: {}", id);
        return "Orden no encontrada";
    }

    /**
     * Elimina una orden permanentemente
     */
    public String delete(String id) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
        
        log.warn("Orden eliminada permanentemente con ID: {}", id);
        return "Orden eliminada en: " + writeResult.toString();
    }
}