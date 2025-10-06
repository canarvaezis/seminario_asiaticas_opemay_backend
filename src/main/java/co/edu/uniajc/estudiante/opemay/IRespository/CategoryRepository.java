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

import co.edu.uniajc.estudiante.opemay.model.Category;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CategoryRepository {

    private static final String COLLECTION_NAME = "categories";

    /**
     * Guarda una categoría en Firestore
     */
    public String save(Category category) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(category.getId())
                .set(category);
        
        log.info("Categoría guardada con ID: {}", category.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Obtiene una categoría por su ID
     */
    public Category getCategoryById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            log.info("Categoría encontrada con ID: {}", id);
            return document.toObject(Category.class);
        }
        
        log.warn("Categoría no encontrada con ID: {}", id);
        return null;
    }

    /**
     * Obtiene una categoría por su nombre
     */
    public Category getCategoryByName(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("name", name)
                .whereEqualTo("active", true)
                .limit(1);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        
        if (!documents.isEmpty()) {
            log.info("Categoría encontrada con nombre: {}", name);
            return documents.get(0).toObject(Category.class);
        }
        
        log.info("No se encontró categoría con nombre: {}", name);
        return null;
    }

    /**
     * Obtiene todas las categorías activas ordenadas por sortOrder
     */
    public List<Category> getAllActiveCategories() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("active", true);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Category> categoryList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            categoryList.add(doc.toObject(Category.class));
        }

        // Ordenar en memoria por sortOrder y luego por name
        categoryList.sort((c1, c2) -> {
            // Primero ordenar por sortOrder
            int sortOrderComparison = Integer.compare(
                c1.getSortOrder() != null ? c1.getSortOrder() : Integer.MAX_VALUE,
                c2.getSortOrder() != null ? c2.getSortOrder() : Integer.MAX_VALUE
            );
            
            if (sortOrderComparison != 0) {
                return sortOrderComparison;
            }
            
            // Si sortOrder es igual, ordenar por name
            String name1 = c1.getName() != null ? c1.getName() : "";
            String name2 = c2.getName() != null ? c2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });

        log.info("Encontradas {} categorías activas", categoryList.size());
        return categoryList;
    }

    /**
     * Obtiene todas las categorías (incluidas las inactivas)
     */
    public List<Category> getAllCategories() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Query query = dbFirestore.collection(COLLECTION_NAME);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Category> categoryList = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            categoryList.add(doc.toObject(Category.class));
        }

        // Ordenar en memoria por sortOrder y luego por name
        categoryList.sort((c1, c2) -> {
            // Primero ordenar por sortOrder
            int sortOrderComparison = Integer.compare(
                c1.getSortOrder() != null ? c1.getSortOrder() : Integer.MAX_VALUE,
                c2.getSortOrder() != null ? c2.getSortOrder() : Integer.MAX_VALUE
            );
            
            if (sortOrderComparison != 0) {
                return sortOrderComparison;
            }
            
            // Si sortOrder es igual, ordenar por name
            String name1 = c1.getName() != null ? c1.getName() : "";
            String name2 = c2.getName() != null ? c2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });

        log.info("Encontradas {} categorías en total", categoryList.size());
        return categoryList;
    }

    /**
     * Actualiza una categoría existente
     */
    public String update(Category category) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME)
                .document(category.getId())
                .set(category);
        
        log.info("Categoría actualizada con ID: {}", category.getId());
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    /**
     * Elimina una categoría (soft delete)
     */
    public String softDelete(String id) throws ExecutionException, InterruptedException {
        Category category = getCategoryById(id);
        if (category != null) {
            category.setActive(false);
            return update(category);
        }
        
        log.warn("Intento de eliminar categoría inexistente con ID: {}", id);
        return "Categoría no encontrada";
    }

    /**
     * Elimina una categoría permanentemente
     */
    public String delete(String id) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
        
        log.warn("Categoría eliminada permanentemente con ID: {}", id);
        return "Categoría eliminada en: " + writeResult.toString();
    }
}