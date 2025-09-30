package co.edu.uniajc.estudiante.opemay.restController;

import java.util.List;
import java.util.concurrent.ExecutionException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemay.Service.CategoryService;
import co.edu.uniajc.estudiante.opemay.dto.CreateCategoryRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdateCategoryRequest;
import co.edu.uniajc.estudiante.opemay.model.Category;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Obtiene todas las categorías activas (público)
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllActiveCategories() {
        try {
            List<Category> categories = categoryService.getAllActiveCategories();
            return ResponseEntity.ok(categories);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener categorías activas", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una categoría por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        try {
            Category category = categoryService.getCategoryById(id);
            
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(category);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener categoría por ID", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Busca una categoría por nombre (público)
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        try {
            Category category = categoryService.getCategoryByName(name);
            
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(category);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener categoría por nombre", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener categoría por nombre: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints administrativos

    /**
     * Crea una nueva categoría (solo administradores)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        try {
            Category category = categoryService.createCategory(
                    request.getName(),
                    request.getDescription(),
                    request.getImageUrl(),
                    request.getSortOrder()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al crear categoría", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza una categoría existente (solo administradores)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, 
                                                 @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            Category category = categoryService.updateCategory(
                    id,
                    request.getName(),
                    request.getDescription(),
                    request.getImageUrl(),
                    request.getSortOrder()
            );
            
            return ResponseEntity.ok(category);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al actualizar categoría", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Activa o desactiva una categoría (solo administradores)
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> toggleCategoryStatus(@PathVariable String id) {
        try {
            Category category = categoryService.toggleCategoryStatus(id);
            return ResponseEntity.ok(category);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al cambiar estado de categoría", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al cambiar estado de categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina una categoría (solo administradores)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al eliminar categoría", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar categoría: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene todas las categorías incluidas las inactivas (solo administradores)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener todas las categorías", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}