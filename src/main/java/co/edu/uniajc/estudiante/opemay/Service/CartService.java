package co.edu.uniajc.estudiante.opemay.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.Product;
import co.edu.uniajc.estudiante.opemay.respository.CartRepository;

@Service
public class CartService {

    private final CartRepository repository;
    private final ProductService productService;

    public CartService(CartRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

    // Agrega o suma cantidad si ya existe
    public List<Cart> addItem(String userId, Cart item) {
        try {
            String productId = item.getProductId();
            if (productId == null || productId.isEmpty()) {
                throw new IllegalArgumentException("productId es obligatorio");
            }

            // Consultar producto para obtener datos correctos
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new RuntimeException("Producto no encontrado: " + productId);
            }

            // Completar información automáticamente
            item.setPrice(product.getPrice());
            item.setName(product.getName());
            item.setDescription(product.getDescription());

            Cart existing = repository.getItem(userId, productId);
            if (existing == null) {
                item.setCreatedAt(new Date());
                item.setUpdatedAt(new Date());
                repository.saveItem(userId, item);
            } else {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                existing.setUpdatedAt(new Date());
                existing.setPrice(product.getPrice());
                existing.setName(product.getName());
                existing.setDescription(product.getDescription());
                repository.saveItem(userId, existing);
            }
            return repository.getItems(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al agregar item al carrito", e);
        }
    }

    // Actualiza cantidad — si quantity <= 0 borra el item
    public List<Cart> updateItemQuantity(String userId, String productId, int quantity) {
        try {
            Cart existing = repository.getItem(userId, productId);
            if (existing == null) {
                throw new RuntimeException("Item no encontrado en el carrito");
            }
            if (quantity <= 0) {
                repository.deleteItem(userId, productId);
            } else {
                existing.setQuantity(quantity);
                existing.setUpdatedAt(new Date());
                repository.saveItem(userId, existing);
            }
            return repository.getItems(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al actualizar item", e);
        }
    }

    // Remover item
    public List<Cart> removeItem(String userId, String productId) {
        try {
            repository.deleteItem(userId, productId);
            return repository.getItems(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al eliminar item", e);
        }
    }

    // Listar items
    public List<Cart> getItems(String userId) {
        try {
            return repository.getItems(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al obtener items", e);
        }
    }

    // Total
    public double getTotal(String userId) {
        try {
            return repository.calculateTotal(userId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al calcular total", e);
        }
    }
}
