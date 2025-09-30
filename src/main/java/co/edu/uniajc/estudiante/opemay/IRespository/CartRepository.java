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

import co.edu.uniajc.estudiante.opemay.model.Cart;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CartRepository {

    private static final String COLLECTION_NAME = "carts";

    /**
     * Guarda un carrito en Firestore
     */
    public String save(Cart cart) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(cart.getId())
                .set(cart);
        
        log.info("Carrito guardado con ID: {}", cart.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Obtiene un carrito por su ID
     */
    public Cart getCartById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            log.info("Carrito encontrado con ID: {}", id);
            return document.toObject(Cart.class);
        }
        
        log.warn("Carrito no encontrado con ID: {}", id);
        return null;
    }

    /**
     * Obtiene el carrito activo de un usuario
     */
    public Cart getActiveCartByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "ACTIVE")
                .whereEqualTo("active", true)
                .limit(1);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        if (!documents.isEmpty()) {
            log.info("Carrito activo encontrado para usuario: {}", userId);
            return documents.get(0).toObject(Cart.class);
        }
        
        log.info("No se encontró carrito activo para usuario: {}", userId);
        return null;
    }

    /**
     * Obtiene todos los carritos de un usuario
     */
    public List<Cart> getCartsByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Cart> cartList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            cartList.add(doc.toObject(Cart.class));
        }

        log.info("Encontrados {} carritos para usuario: {}", cartList.size(), userId);
        return cartList;
    }

    /**
     * Obtiene todos los carritos
     */
    public List<Cart> getAllCarts() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Cart> cartList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            cartList.add(doc.toObject(Cart.class));
        }

        log.info("Encontrados {} carritos en total", cartList.size());
        return cartList;
    }

    /**
     * Actualiza un carrito existente
     */
    public String update(Cart cart) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(cart.getId())
                .set(cart);
        
        log.info("Carrito actualizado con ID: {}", cart.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Elimina un carrito (soft delete)
     */
    public String softDelete(String id) throws ExecutionException, InterruptedException {
        Cart cart = getCartById(id);
        if (cart != null) {
            cart.setActive(false);
            cart.setStatus("DELETED");
            return update(cart);
        }
        
        log.warn("Intento de eliminar carrito inexistente con ID: {}", id);
        return "Carrito no encontrado";
    }

    /**
     * Elimina un carrito permanentemente
     */
    public String delete(String id) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
        
        log.warn("Carrito eliminado permanentemente con ID: {}", id);
        return "Carrito eliminado en: " + writeResult.toString();
    }

    /**
     * Obtiene carritos por estado
     */
    public List<Cart> getCartsByStatus(String status) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status)
                .whereEqualTo("active", true);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Cart> cartList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            cartList.add(doc.toObject(Cart.class));
        }

        log.info("Encontrados {} carritos con estado: {}", cartList.size(), status);
        return cartList;
    }
}