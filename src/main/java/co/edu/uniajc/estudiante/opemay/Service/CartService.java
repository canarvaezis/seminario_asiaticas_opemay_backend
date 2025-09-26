package co.edu.uniajc.estudiante.opemay.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.IRespository.CartRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductRepository;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;
import co.edu.uniajc.estudiante.opemay.model.Product;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Obtiene o crea el carrito activo de un usuario
     */
    public Cart getOrCreateActiveCart(String userId) throws ExecutionException, InterruptedException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario no puede estar vacío");
        }

        Cart activeCart = cartRepository.getActiveCartByUserId(userId);
        
        if (activeCart == null) {
            log.info("Creando nuevo carrito para usuario: {}", userId);
            activeCart = Cart.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .status("ACTIVE")
                    .build();
            
            cartRepository.save(activeCart);
        }
        
        return activeCart;
    }

    /**
     * Agrega un producto al carrito
     */
    public Cart addProductToCart(String userId, String productId, Integer quantity) 
            throws ExecutionException, InterruptedException {
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // Verificar que el producto existe
        Product product = productRepository.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        if (!product.getActive()) {
            throw new IllegalArgumentException("El producto no está disponible");
        }

        // Obtener o crear carrito
        Cart cart = getOrCreateActiveCart(userId);

        // Crear item del carrito
        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .imageUrl(product.getImageUrl())
                .build();

        // Agregar item al carrito
        cart.addItem(cartItem);

        // Guardar carrito
        cartRepository.update(cart);
        
        log.info("Producto {} agregado al carrito del usuario {}", productId, userId);
        return cart;
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    public Cart updateProductQuantity(String userId, String productId, Integer quantity) 
            throws ExecutionException, InterruptedException {
        
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Cart cart = getOrCreateActiveCart(userId);
        
        if (quantity == 0) {
            // Si la cantidad es 0, remover el producto
            cart.removeItem(productId);
        } else {
            // Buscar el item y actualizar cantidad
            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);
                    
            if (existingItem != null) {
                existingItem.setQuantity(quantity);
                existingItem.setUpdatedAt(Timestamp.now());
                cart.calculateTotals();
                cart.setUpdatedAt(Timestamp.now());
            } else {
                throw new IllegalArgumentException("Producto no encontrado en el carrito");
            }
        }

        cartRepository.update(cart);
        
        log.info("Cantidad del producto {} actualizada en el carrito del usuario {}", productId, userId);
        return cart;
    }

    /**
     * Remueve un producto del carrito
     */
    public Cart removeProductFromCart(String userId, String productId) 
            throws ExecutionException, InterruptedException {
        
        Cart cart = getOrCreateActiveCart(userId);
        cart.removeItem(productId);
        
        cartRepository.update(cart);
        
        log.info("Producto {} removido del carrito del usuario {}", productId, userId);
        return cart;
    }

    /**
     * Limpia todo el carrito
     */
    public Cart clearCart(String userId) throws ExecutionException, InterruptedException {
        Cart cart = getOrCreateActiveCart(userId);
        cart.clearCart();
        
        cartRepository.update(cart);
        
        log.info("Carrito del usuario {} limpiado", userId);
        return cart;
    }

    /**
     * Obtiene el carrito activo de un usuario
     */
    public Cart getActiveCart(String userId) throws ExecutionException, InterruptedException {
        return getOrCreateActiveCart(userId);
    }

    /**
     * Obtiene todos los carritos de un usuario
     */
    public List<Cart> getUserCarts(String userId) throws ExecutionException, InterruptedException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario no puede estar vacío");
        }
        
        return cartRepository.getCartsByUserId(userId);
    }

    /**
     * Completa el carrito (lo marca como completado)
     */
    public Cart completeCart(String userId) throws ExecutionException, InterruptedException {
        Cart cart = cartRepository.getActiveCartByUserId(userId);
        
        if (cart == null) {
            throw new IllegalArgumentException("No hay carrito activo para completar");
        }
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        
        cart.setStatus("COMPLETED");
        cart.setUpdatedAt(Timestamp.now());
        
        cartRepository.update(cart);
        
        log.info("Carrito del usuario {} completado", userId);
        return cart;
    }

    /**
     * Abandona el carrito (lo marca como abandonado)
     */
    public Cart abandonCart(String userId) throws ExecutionException, InterruptedException {
        Cart cart = cartRepository.getActiveCartByUserId(userId);
        
        if (cart == null) {
            throw new IllegalArgumentException("No hay carrito activo para abandonar");
        }
        
        cart.setStatus("ABANDONED");
        cart.setActive(false);
        cart.setUpdatedAt(Timestamp.now());
        
        cartRepository.update(cart);
        
        log.info("Carrito del usuario {} abandonado", userId);
        return cart;
    }

    /**
     * Obtiene un carrito por ID
     */
    public Cart getCartById(String cartId) throws ExecutionException, InterruptedException {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del carrito no puede estar vacío");
        }
        
        return cartRepository.getCartById(cartId);
    }

    /**
     * Obtiene todos los carritos (para administradores)
     */
    public List<Cart> getAllCarts() throws ExecutionException, InterruptedException {
        return cartRepository.getAllCarts();
    }

    /**
     * Obtiene carritos por estado
     */
    public List<Cart> getCartsByStatus(String status) throws ExecutionException, InterruptedException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        
        return cartRepository.getCartsByStatus(status);
    }
}