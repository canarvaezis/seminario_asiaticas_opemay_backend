package co.edu.uniajc.estudiante.opemayfruitshop.service;

import co.edu.uniajc.estudiante.opemayfruitshop.model.User;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // ✅ Crear usuario en Firebase Auth + Firestore + carrito vacío
    public User createUser(String email, String password, String name, String address, String phone) {
        try {
            // 1. Crear en Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // 2. Crear en colección "users"
            User user = new User(userRecord.getUid(), email, name, address, phone);
            repository.saveUser(user);

            // 3. Crear carrito vacío en subcolección
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("createdAt", Instant.now().toString());
            cartData.put("updatedAt", Instant.now().toString());

            db.collection("users")
              .document(user.getId())
              .collection("cart")
              .document("cart")
              .set(cartData);

            return user;

        } catch (Exception e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    // ✅ Leer usuario
    public User getUser(String id) {
        try {
            return repository.getUser(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error getting user", e);
        }
    }

    // ✅ Listar usuarios
    public List<User> getAllUsers() {
        try {
            return repository.getAllUsers();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error getting all users", e);
        }
    }

    // ✅ Actualizar usuario
    public User updateUser(User user) {
        try {
            repository.saveUser(user);
            return user;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    // ✅ Eliminar usuario (Firestore + Auth)
    public void deleteUser(String id) {
        try {
            FirebaseAuth.getInstance().deleteUser(id); // elimina de Auth
            repository.deleteUser(id);                 // elimina de Firestore
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
}
