package co.edu.uniajc.estudiante.opemay.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

import co.edu.uniajc.estudiante.opemay.IRespository.CategoryRepository;
import co.edu.uniajc.estudiante.opemay.dto.CategoryCreateDTO;
import co.edu.uniajc.estudiante.opemay.dto.CategoryUpdateDTO;
import co.edu.uniajc.estudiante.opemay.model.Category;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    private Firestore firestore;
    
    public CategoryService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    /**
     * Crea una nueva categoría
     */
    public Category createCategory(String name, String description, String imageUrl, Integer sortOrder) 
            throws ExecutionException, InterruptedException {
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        // Verificar que no existe una categoría con el mismo nombre
        Category existingCategory = categoryRepository.getCategoryByName(name.trim());
        if (existingCategory != null) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + name);
        }

        Category category = Category.builder()
                .id(UUID.randomUUID().toString())
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .active(true)
                .build();

        categoryRepository.save(category);
        
        log.info("Categoría creada: {} con ID: {}", name, category.getId());
        return category;
    }

    /**
     * Obtiene una categoría por ID
     */
    public Category getCategoryById(String id) throws ExecutionException, InterruptedException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la categoría no puede estar vacío");
        }
        
        return categoryRepository.getCategoryById(id);
    }

    /**
     * Obtiene una categoría por nombre
     */
    public Category getCategoryByName(String name) throws ExecutionException, InterruptedException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        return categoryRepository.getCategoryByName(name.trim());
    }

    /**
     * Obtiene todas las categorías activas
     */
    public List<Category> getAllActiveCategories() throws ExecutionException, InterruptedException {
        return categoryRepository.getAllActiveCategories();
    }

    /**
     * Obtiene todas las categorías (para administradores)
     */
    public List<Category> getAllCategories() throws ExecutionException, InterruptedException {
        return categoryRepository.getAllCategories();
    }

    /**
     * Actualiza una categoría existente
     */
    public Category updateCategory(String id, String name, String description, String imageUrl, Integer sortOrder) 
            throws ExecutionException, InterruptedException {
        
        Category category = getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }

        // Si se cambió el nombre, verificar que no exista otra categoría con ese nombre
        if (name != null && !name.trim().equals(category.getName())) {
            Category existingCategory = categoryRepository.getCategoryByName(name.trim());
            if (existingCategory != null && !existingCategory.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + name);
            }
            category.setName(name.trim());
        }

        if (description != null) {
            category.setDescription(description.trim());
        }
        
        if (imageUrl != null) {
            category.setImageUrl(imageUrl);
        }
        
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
        
        category.setUpdatedAt(Timestamp.now());
        
        categoryRepository.update(category);
        
        log.info("Categoría actualizada: {}", category.getName());
        return category;
    }

    /**
     * Activa o desactiva una categoría
     */
    public Category toggleCategoryStatus(String id) throws ExecutionException, InterruptedException {
        Category category = getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        
        category.setActive(!category.getActive());
        category.setUpdatedAt(Timestamp.now());
        
        categoryRepository.update(category);
        
        log.info("Categoría {} {}: {}", category.getName(), 
                category.getActive() ? "activada" : "desactivada", category.getId());
        return category;
    }

    /**
     * Elimina una categoría (soft delete)
     */
    public void deleteCategory(String id) throws ExecutionException, InterruptedException {
        Category category = getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        
        categoryRepository.softDelete(id);
        
        log.info("Categoría eliminada: {}", category.getName());
    }

    /**
     * Valida los datos de una categoría
     */
    public void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("La categoría no puede ser null");
        }
        
        if (!category.isValid()) {
            throw new IllegalArgumentException("Los datos de la categoría no son válidos");
        }
    }

    // Métodos compatibles para los tests
    public String createCategory(CategoryCreateDTO dto) {
        return createCategory(dto.getName(), dto.getDescription(), dto.getSlug());
    }

    public String updateCategory(String categoryId, CategoryUpdateDTO dto) {
        return updateCategory(categoryId, dto.getName(), dto.getDescription(), dto.getSlug());
    }

    public Category getCategoryBySlug(String slug) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("categories")
            .whereEqualTo("slug", slug)
            .get();
        
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        
        if (documents.isEmpty()) {
            return null;
        }
        
        QueryDocumentSnapshot document = documents.get(0);
        Category category = document.toObject(Category.class);
        category.setId(document.getId());
        return category;
    }
}