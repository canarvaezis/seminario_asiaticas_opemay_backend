package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import co.edu.uniajc.estudiante.opemayfruitshop.model.User;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea el usuario en Firebase Auth (email/password) y guarda los datos públicos
     * del usuario en Firestore (sin guardar la contraseña).
     */
    public User createUser(String email, String password, String name, String address, String phone) {
        try {
            // 1) Crear en Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // 2) Construir objeto User para Firestore (sin contraseña)
            User user = new User();
            user.setId(userRecord.getUid());
            user.setEmail(email);
            user.setName(name);
            user.setAddress(address);
            user.setPhone(phone);

            // 3) Guardar en Firestore
            repository.saveUser(user);

            return user;

        } catch (ExecutionException | InterruptedException e) {
            // Si hubo problemas guardando en Firestore
            throw new RuntimeException("Error saving user in Firestore", e);
        } catch (Exception e) {
            // Errores de FirebaseAuth (por ejemplo email ya registrado, formato inválido, etc.)
            throw new RuntimeException("Error creating user in Firebase Auth", e);
        }
    }

    public User getUser(String id) {
        try {
            return repository.getUser(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error getting user", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            return repository.getAllUsers();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error getting all users", e);
        }
    }

    public User updateUser(User user) {
        try {
            // 1. Traemos el usuario actual desde Firestore
            User existing = repository.getUser(user.getId());
            if (existing == null) {
                throw new RuntimeException("User not found");
            }
        
            // 2. Respetamos email y password -> NO se actualizan aquí
            user.setEmail(existing.getEmail());
            user.setPassword(null); // nunca guardamos password en Firestore
        
            // 3. Solo actualizamos datos editables
            existing.setName(user.getName());
            existing.setAddress(user.getAddress());
            existing.setPhone(user.getPhone());
        
            // 4. Guardamos en Firestore
            repository.saveUser(existing);
        
            return existing;
        
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }


    public void deleteUser(String id) {
        try {
            FirebaseAuth.getInstance().deleteUser(id); // elimina de Auth
            repository.deleteUser(id);                 // elimina de Firestore
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
}
