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

import co.edu.uniajc.estudiante.opemay.model.User;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserRepository {

    private static final String COLLECTION_NAME = "users";

    public String save(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public User getUserByUsername(String username) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME).whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        if (!documents.isEmpty()) {
            return documents.get(0).toObject(User.class);
        }
        return null;
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        System.out.println("🔍 UserRepository: Buscando usuario con email: '" + email + "'");
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME).whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        System.out.println("🔍 UserRepository: Documentos encontrados: " + documents.size());
        
        if (!documents.isEmpty()) {
            QueryDocumentSnapshot doc = documents.get(0);
            System.out.println("🔍 UserRepository: Documento encontrado - ID: " + doc.getId());
            System.out.println("🔍 UserRepository: Datos del documento: " + doc.getData());
            User user = doc.toObject(User.class);
            System.out.println("🔍 UserRepository: Usuario convertido - Email: " + user.getEmail());
            return user;
        }
        System.out.println("🔍 UserRepository: No se encontró usuario con email: '" + email + "'");
        return null;
    }

    public User getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(User.class);
        }
        return null;
    }

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<User> userList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            userList.add(doc.toObject(User.class));
        }

        return userList;
    }

    public String update(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String delete(String id) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
        return "Usuario eliminado en: " + writeResult.toString();
    }
}
