package co.edu.uniajc.estudiante.opemay.restController;

import java.util.List;
import java.util.Map;

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

import co.edu.uniajc.estudiante.opemay.Service.ProductService;
import co.edu.uniajc.estudiante.opemay.model.Product;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/save")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        try {
            log.info("Recibida petición para guardar producto: {}", product.getName());
            
            // Validar datos del producto
            if (!product.isValid()) {
                log.error("Producto inválido: {}", product);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            Product savedProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            log.error("Error guardando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            log.info("Recibida petición para obtener todos los productos");
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        try {
            log.info("Recibida petición para obtener producto con ID: {}", id);
            Product product = productService.getProductById(id);
            
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error obteniendo producto por ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 //trae todos los productos de una categoria
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryId) {
        try {
            log.info("Recibida petición para obtener productos de categoría: {}", categoryId);
            List<Product> products = productService.getProductsByCategory(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos por categoría: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Product product) {
        try {
            log.info("Recibida petición para actualizar producto con ID: {}", id);
            
            // Validar datos del producto
            if (!product.isValid()) {
                log.error("Producto inválido para actualización: {}", product);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Datos del producto inválidos"));
            }
            
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
            
        } catch (IllegalArgumentException e) {
            log.error("Error de validación actualizando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error actualizando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("    ('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            log.info("Recibida petición para eliminar producto con ID: {}", id);
            
            boolean deleted = productService.deleteProduct(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "message", "Producto eliminado correctamente",
                    "id", id
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo eliminar el producto"));
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Error de validación eliminando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error eliminando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> hardDeleteProduct(@PathVariable String id) {
        try {
            log.info("Recibida petición para eliminación permanente de producto con ID: {}", id);
            
            boolean deleted = productService.hardDeleteProduct(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "message", "Producto eliminado permanentemente",
                    "id", id
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo eliminar permanentemente el producto"));
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Error de validación eliminando permanentemente producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error eliminando permanentemente producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}