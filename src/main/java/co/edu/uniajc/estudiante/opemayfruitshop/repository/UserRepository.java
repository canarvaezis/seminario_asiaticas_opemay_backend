package co.edu.uniajc.estudiante.opemayfruitshop.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;
import co.edu.uniajc.estudiante.opemayfruitshop.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private static final String COLLECTION = "users";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // ✅ Crear o actualizar usuario
    public void saveUser(User user) throws ExecutionException, InterruptedException {
        getFirestore()
                .collection(COLLECTION)
                .document(user.getId())
                .set(user)
                .get();
    }

    // ✅ Leer usuario por ID
    public User getUser(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = getFirestore()
                .collection(COLLECTION)
                .document(id)
                .get()
                .get();
        return snapshot.exists() ? snapshot.toObject(User.class) : null;
    }

    // ✅ Listar todos los usuarios
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getFirestore()
                .collection(COLLECTION)
                .get();
        List<User> users = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            User user = doc.toObject(User.class);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    // ✅ Eliminar usuario de Firestore + carrito
    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        // Borrar carrito asociado
        ApiFuture<WriteResult> deleteCart = db
                .collection(COLLECTION)
                .document(id)
                .collection("cart")
                .document("cart")
                .delete();

        deleteCart.get(); // esperar escritura

        // Borrar documento del usuario
        db.collection(COLLECTION)
                .document(id)
                .delete()
                .get();
    }
}
