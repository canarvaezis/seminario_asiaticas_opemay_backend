package co.edu.uniajc.estudiante.opemay.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import co.edu.uniajc.estudiante.opemay.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de productos con integración a Firebase Firestore
 * Implementa patrones Circuit Breaker para resiliencia
 * 
 * @author OpemAy Team
 * @version 1.0
 * @since 2025-01-26
 */
@Service
@Slf4j
public class ProductService {

    private final Firestore firestore;
    
    // 🔹 Constantes SonarQube-compliant
    private static final String CIRCUIT_BREAKER_NAME = "productService";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String ERROR_FIREBASE_SAVE = "Error saving product in Firebase";
    private static final String ERROR_FIREBASE_RETRIEVE = "Error retrieving products from Firebase";
    private static final String ERROR_FIREBASE_GET_BY_ID = "Error retrieving product by ID";
    private static final String FALLBACK_NAME = "Producto no disponible temporalmente";
    private static final String FALLBACK_DESCRIPTION = "Servicio no disponible";
    private static final double FALLBACK_PRICE = 0.0;

    /**
     * Constructor que inyecta la dependencia de Firestore
     * 
     * @param firestore instancia de Firestore configurada
     */
    public ProductService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Crea un nuevo producto en Firestore con Circuit Breaker
     * 
     * @param product producto a crear
     * @return producto creado con ID asignado
     * @throws IllegalArgumentException si el producto es nulo o inválido
     * @throws RuntimeException si hay error en la persistencia
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createProductFallback")
    public Product createProduct(Product product) {
        validateProduct(product);
        
        try {
            log.info("Creando producto: {}", product.getName());
            
            String productId = generateProductId(product);
            product.setId(productId);
            product.setUpdatedAt(Timestamp.now());
            
            ApiFuture<WriteResult> future = firestore.collection(PRODUCTS_COLLECTION)
                    .document(productId)
                    .set(product);

            WriteResult result = future.get();
            log.info("Producto guardado en: {}", result.getUpdateTime());
            return product;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Proceso interrumpido guardando producto: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_SAVE, e);
        } catch (ExecutionException e) {
            log.error("Error ejecutando operación en Firebase: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_SAVE, e);
        }
    }

    /**
     * Valida que el producto tenga los campos requeridos
     * 
     * @param product producto a validar
     * @throws IllegalArgumentException si el producto es inválido
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor o igual a 0");
        }
    }

    /**
     * Genera un ID para el producto si no existe
     * 
     * @param product producto
     * @return ID del producto
     */
    private String generateProductId(Product product) {
        return product.getId() != null ? product.getId() : 
            firestore.collection(PRODUCTS_COLLECTION).document().getId();
    }

    /**
     * Obtiene todos los productos activos desde Firestore con Circuit Breaker
     * 
     * @return lista de productos encontrados (nunca null)
     * @throws RuntimeException si hay error en la consulta
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getAllProductsFallback")
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        try {
            log.info("Obteniendo todos los productos");
            ApiFuture<QuerySnapshot> future = firestore.collection(PRODUCTS_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot doc : documents) {
                processDocument(doc, products);
            }
            
            log.info("Se obtuvieron {} productos", products.size());
            return products;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Proceso interrumpido obteniendo productos: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_RETRIEVE, e);
        } catch (ExecutionException e) {
            log.error("Error ejecutando consulta de productos: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_RETRIEVE, e);
        }
    }

    /**
     * Procesa un documento individual y lo añade a la lista si es válido
     * 
     * @param doc documento de Firestore
     * @param products lista donde añadir el producto convertido
     */
    private void processDocument(QueryDocumentSnapshot doc, List<Product> products) {
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

    /**
     * Obtiene un producto por su ID desde Firestore con Circuit Breaker
     * 
     * @param id identificador único del producto
     * @return producto encontrado o null si no existe
     * @throws IllegalArgumentException si el ID es nulo o vacío
     * @throws RuntimeException si hay error en la consulta
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductByIdFallback")
    public Product getProductById(String id) {
        validateProductId(id);
        
        try {
            log.info("Obteniendo producto por ID: {}", id);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = 
                firestore.collection(PRODUCTS_COLLECTION).document(id).get();
            
            com.google.cloud.firestore.DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                return processDocumentSnapshot(document);
            } else {
                log.warn("Producto no encontrado con ID: {}", id);
                return null;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Proceso interrumpido obteniendo producto por ID: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_GET_BY_ID, e);
        } catch (ExecutionException e) {
            log.error("Error ejecutando consulta por ID: {}", e.getMessage());
            throw new RuntimeException(ERROR_FIREBASE_GET_BY_ID, e);
        }
    }

    /**
     * Valida que el ID del producto sea válido
     * 
     * @param id ID a validar
     * @throws IllegalArgumentException si el ID es inválido
     */
    private void validateProductId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo o vacío");
        }
    }

    /**
     * Procesa un DocumentSnapshot y lo convierte a Product
     * 
     * @param document documento de Firestore
     * @return producto convertido o null si hay error
     */
    private Product processDocumentSnapshot(com.google.cloud.firestore.DocumentSnapshot document) {
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
    }

    // ========== MÉTODOS FALLBACK PARA CIRCUIT BREAKER ==========

    /**
     * Método fallback para createProduct cuando el Circuit Breaker se activa
     * 
     * @param product producto que se intentaba crear
     * @param ex excepción que causó la activación del Circuit Breaker
     * @return producto fallback con datos por defecto
     */
    public Product createProductFallback(Product product, Exception ex) {
        log.error("Circuit Breaker activado para createProduct. Error: {}", ex.getMessage());
        return Product.builder()
                .id("fallback-id")
                .name(FALLBACK_NAME)
                .price(FALLBACK_PRICE)
                .description(FALLBACK_DESCRIPTION)
                .active(false)
                .createdAt(Timestamp.now())
                .updatedAt(Timestamp.now())
                .build();
    }

    /**
     * Método fallback para getAllProducts cuando el Circuit Breaker se activa
     * 
     * @param ex excepción que causó la activación del Circuit Breaker
     * @return lista con un producto fallback
     */
    public List<Product> getAllProductsFallback(Exception ex) {
        log.error("Circuit Breaker activado para getAllProducts. Error: {}", ex.getMessage());
        return List.of(Product.builder()
                .id("fallback-id")
                .name("Productos no disponibles")
                .price(FALLBACK_PRICE)
                .description("Servicio temporalmente no disponible")
                .active(false)
                .createdAt(Timestamp.now())
                .updatedAt(Timestamp.now())
                .build());
    }

    /**
     * Método fallback para getProductById cuando el Circuit Breaker se activa
     * 
     * @param id ID del producto que se intentaba obtener
     * @param ex excepción que causó la activación del Circuit Breaker
     * @return producto fallback con el ID solicitado
     */
    public Product getProductByIdFallback(String id, Exception ex) {
        log.error("Circuit Breaker activado para getProductById. Error: {}", ex.getMessage());
        return Product.builder()
                .id(id)
                .name("Producto no disponible")
                .price(FALLBACK_PRICE)
                .description("Servicio temporalmente no disponible")
                .active(false)
                .createdAt(Timestamp.now())
                .updatedAt(Timestamp.now())
                .build();
    }

    // ========== MÉTODOS DE CONVERSIÓN ==========

    /**
     * Convierte un QueryDocumentSnapshot a un objeto Product
     * Maneja las conversiones de tipo de manera segura
     * 
     * @param doc documento de Firestore del tipo QueryDocumentSnapshot
     * @return producto convertido o null si hay error
     */
    private Product convertDocumentToProduct(QueryDocumentSnapshot doc) {
        if (doc == null) {
            log.error("Documento nulo recibido para conversión");
            return null;
        }
        
        try {
            return buildProductFromDocument(doc.getId(), doc);
        } catch (Exception e) {
            log.error("Error convirtiendo documento {}: {}", doc.getId(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte un DocumentSnapshot a un objeto Product
     * Maneja las conversiones de tipo de manera segura
     * 
     * @param doc documento de Firestore del tipo DocumentSnapshot
     * @return producto convertido o null si hay error
     */
    private Product convertDocumentSnapshotToProduct(com.google.cloud.firestore.DocumentSnapshot doc) {
        if (doc == null) {
            log.error("DocumentSnapshot nulo recibido para conversión");
            return null;
        }
        
        try {
            return buildProductFromDocument(doc.getId(), doc);
        } catch (Exception e) {
            log.error("Error convirtiendo documento {}: {}", doc.getId(), e.getMessage());
            return null;
        }
    }

    /**
     * Construye un objeto Product a partir de los datos del documento
     * Método común para ambos tipos de documento de Firestore
     * 
     * @param id ID del documento
     * @param doc documento con los datos
     * @return producto construido
     */
    private Product buildProductFromDocument(String id, com.google.cloud.firestore.DocumentSnapshot doc) {
        String name = doc.getString(FIELD_NAME);
        String description = doc.getString(FIELD_DESCRIPTION);
        Double price = doc.getDouble(FIELD_PRICE);
        Boolean active = doc.getBoolean(FIELD_ACTIVE);
        
        // Manejar conversión de timestamps de manera segura
        Timestamp createdAt = convertToTimestamp(doc.get(FIELD_CREATED_AT));
        Timestamp updatedAt = convertToTimestamp(doc.get(FIELD_UPDATED_AT));
        
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price != null ? price : FALLBACK_PRICE)
                .active(active != null ? active : Boolean.TRUE)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    
    /**
     * Convierte varios tipos de datos a Timestamp de manera segura
     * Maneja diferentes formatos que pueden venir de Firestore
     * 
     * @param timestampObj objeto que representa un timestamp
     * @return timestamp convertido o timestamp actual como fallback
     */
    private Timestamp convertToTimestamp(Object timestampObj) {
        if (timestampObj == null) {
            return Timestamp.now();
        }
        
        if (timestampObj instanceof Timestamp timestamp) {
            return timestamp;
        }
        
        // Si es un Map (formato LocalDateTime serializado), usar timestamp actual
        if (timestampObj instanceof Map) {
            log.warn("Encontrado Map en timestamp, usando timestamp actual");
            return Timestamp.now();
        }
        
        // Fallback: usar timestamp actual para cualquier otro tipo
        log.warn("Tipo de timestamp no reconocido: {}, usando timestamp actual", 
                timestampObj.getClass().getSimpleName());
        return Timestamp.now();
    }

    /**
     * Obtiene productos por categoría con Circuit Breaker
     * 
     * @param categoryId ID de la categoría
     * @return lista de productos de la categoría
     */
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getProductsByCategoryFallback")
    public List<Product> getProductsByCategory(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de categoría no puede estar vacío");
        }
        
        try {
            log.info("Obteniendo productos para categoría: {}", categoryId);
            
            ApiFuture<QuerySnapshot> future = firestore.collection(PRODUCTS_COLLECTION)
                    .whereEqualTo("categoryId", categoryId)
                    .whereEqualTo("active", true)
                    .orderBy("name", Query.Direction.ASCENDING)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Product> products = documents.stream()
                    .map(this::convertDocumentToProduct)
                    .filter(product -> product != null)
                    .collect(Collectors.toList());
            
            log.info("Encontrados {} productos para categoría {}", products.size(), categoryId);
            return products;
            
        } catch (InterruptedException e) {
            log.error("Operación interrumpida obteniendo productos por categoría: {}", categoryId);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error obteniendo productos por categoría: " + categoryId, e);
        } catch (ExecutionException e) {
            log.error("Error ejecutando consulta para categoría {}: {}", categoryId, e.getMessage());
            throw new RuntimeException("Error obteniendo productos por categoría: " + categoryId, e);
        }
    }

    /**
     * Fallback para getProductsByCategory
     */
    public List<Product> getProductsByCategoryFallback(String categoryId, Exception e) {
        log.warn("Fallback activado para getProductsByCategory con categoría: {}. Error: {}", 
                categoryId, e.getMessage());
        return new ArrayList<>();
    }
}
