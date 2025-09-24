package co.edu.uniajc.estudiante.opemay.Service;

import com.google.cloud.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemay.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

    private final Firestore firestore;
    private static final String CIRCUIT_BREAKER_NAME = "productService";

    public ProductService(Firestore firestore) {
        this.firestore = firestore;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createProductFallback")
    public Product createProduct(Product product) {
        try {
            log.info("Creando producto: {}", product.getName());
            
            // Asignar ID si no existe
            String productId = product.getId() != null ? product.getId() : 
                firestore.collection("products").document().getId();
            product.setId(productId);
            
            // Actualizar timestamp
            product.setUpdatedAt(Timestamp.now());
            
            // Guardar en la colección "products"
            ApiFuture<WriteResult> future = firestore.collection("products")
                    .document(productId)
                    .set(product);

            log.info("Producto guardado en: {}", future.get().getUpdateTime());
            return product;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error guardando producto en Firebase: {}", e.getMessage());
            throw new RuntimeException("Error saving product in Firebase", e);
        }
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getAllProductsFallback")
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            log.info("Obteniendo todos los productos");
            ApiFuture<QuerySnapshot> future = firestore.collection("products").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot doc : documents) {
                try {
                    Product product = convertDocumentToProduct(doc);
                    if (product != null) {
                        products.add(product);
                    }
                } catch (Exception e) {
                    log.error("Error convirtiendo documento a Product: {}", e.getMessage());
                    // Continuar con el siguiente documento
                }
            }
            
            log.info("Se obtuvieron {} productos", products.size());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error obteniendo productos de Firebase: {}", e.getMessage());
            throw new RuntimeException("Error retrieving products from Firebase", e);
        }
        return products;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductByIdFallback")
    public Product getProductById(String id) {
        try {
            log.info("Obteniendo producto por ID: {}", id);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = 
                firestore.collection("products").document(id).get();
            
            com.google.cloud.firestore.DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                try {
                    Product product = convertDocumentSnapshotToProduct(document);
                    if (product != null) {
                        log.info("Producto encontrado: {}", product.getName());
                    } else {
                        log.warn("El documento existe pero no se pudo convertir a Product");
                    }
                    return product;
                } catch (Exception e) {
                    log.error("Error convirtiendo documento a Product: {}", e.getMessage());
                    return null;
                }
            } else {
                log.warn("Producto no encontrado con ID: {}", id);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error obteniendo producto por ID: {}", e.getMessage());
            throw new RuntimeException("Error retrieving product by ID", e);
        }
    }

    // Métodos Fallback para Circuit Breaker
    public Product createProductFallback(Product product, Exception ex) {
        log.error("Circuit Breaker activado para createProduct. Error: {}", ex.getMessage());
        return Product.builder()
                .id("fallback-id")
                .name("Producto no disponible temporalmente")
                .price(0.0)
                .description("Servicio no disponible")
                .active(false)
                .build();
    }

    public List<Product> getAllProductsFallback(Exception ex) {
        log.error("Circuit Breaker activado para getAllProducts. Error: {}", ex.getMessage());
        return List.of(Product.builder()
                .id("fallback-id")
                .name("Productos no disponibles")
                .price(0.0)
                .description("Servicio temporalmente no disponible")
                .active(false)
                .build());
    }

    /**
     * Convierte un documento de Firestore a un objeto Product manejando las conversiones de tipo
     */
    private Product convertDocumentToProduct(QueryDocumentSnapshot doc) {
        try {
            String id = doc.getId();
            String name = doc.getString("name");
            String description = doc.getString("description");
            Double price = doc.getDouble("price");
            Boolean active = doc.getBoolean("active");
            
            // Manejar conversión de timestamps
            Timestamp createdAt = convertToTimestamp(doc.get("createdAt"));
            Timestamp updatedAt = convertToTimestamp(doc.get("updatedAt"));
            
            return Product.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price != null ? price : 0.0)
                    .active(active != null ? active : true)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        } catch (Exception e) {
            log.error("Error convirtiendo documento {}: {}", doc.getId(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte un DocumentSnapshot a un objeto Product manejando las conversiones de tipo
     */
    private Product convertDocumentSnapshotToProduct(com.google.cloud.firestore.DocumentSnapshot doc) {
        try {
            String id = doc.getId();
            String name = doc.getString("name");
            String description = doc.getString("description");
            Double price = doc.getDouble("price");
            Boolean active = doc.getBoolean("active");
            
            // Manejar conversión de timestamps
            Timestamp createdAt = convertToTimestamp(doc.get("createdAt"));
            Timestamp updatedAt = convertToTimestamp(doc.get("updatedAt"));
            
            return Product.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price != null ? price : 0.0)
                    .active(active != null ? active : true)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        } catch (Exception e) {
            log.error("Error convirtiendo documento {}: {}", doc.getId(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte varios tipos de datos a Timestamp
     */
    private Timestamp convertToTimestamp(Object timestampObj) {
        if (timestampObj == null) {
            return Timestamp.now();
        }
        
        if (timestampObj instanceof Timestamp) {
            return (Timestamp) timestampObj;
        }
        
        // Si es un HashMap (formato LocalDateTime serializado), usar timestamp actual
        if (timestampObj instanceof java.util.Map) {
            log.warn("Encontrado HashMap en timestamp, usando timestamp actual");
            return Timestamp.now();
        }
        
        // Fallback: usar timestamp actual
        log.warn("Tipo de timestamp no reconocido: {}, usando timestamp actual", timestampObj.getClass());
        return Timestamp.now();
    }

    public Product getProductByIdFallback(String id, Exception ex) {
        log.error("Circuit Breaker activado para getProductById. Error: {}", ex.getMessage());
        return Product.builder()
                .id(id)
                .name("Producto no disponible")
                .price(0.0)
                .description("Servicio temporalmente no disponible")
                .active(false)
                .build();
    }
}
