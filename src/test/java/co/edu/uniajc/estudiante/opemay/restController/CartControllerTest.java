package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;

/**
 * Tests básicos para verificar la funcionalidad del carrito
 * sin dependencias de Spring Boot para evitar problemas de configuración
 */
class CartControllerTest {

    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testCartItem = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(3)
                .build();

        testCart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .status("ACTIVE")
                .build();
        testCart.addItem(testCartItem);
    }

    @Test
    void testCartItemCreation() {
        // Test simple para verificar la creación de CartItem
        CartItem item = CartItem.builder()
                .productId("test-product")
                .productName("Test Product")
                .price(10.0)
                .quantity(2)
                .build();
        
        assertNotNull(item);
        assertEquals("test-product", item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(10.0, item.getPrice());
        assertEquals(2, item.getQuantity());
        assertEquals(20.0, item.getSubtotal());
    }

    @Test 
    void testCartCreation() {
        // Test simple para verificar la creación de Cart
        Cart cart = Cart.builder()
                .id("test-cart")
                .userId("test-user")
                .status("ACTIVE")
                .build();
        
        assertNotNull(cart);
        assertEquals("test-cart", cart.getId());
        assertEquals("test-user", cart.getUserId());
        assertEquals("ACTIVE", cart.getStatus());
        assertTrue(cart.getActive());
    }

    @Test
    void testCartWithItems() {
        // Test para verificar el cálculo de totales
        Cart cart = Cart.builder()
                .id("cart-with-items")
                .userId("user-1")
                .status("ACTIVE")
                .build();
        
        CartItem item1 = CartItem.builder()
                .productId("product-1")
                .productName("Producto 1")
                .price(5.0)
                .quantity(2)
                .build();
        
        CartItem item2 = CartItem.builder()
                .productId("product-2")
                .productName("Producto 2")
                .price(3.0)
                .quantity(3)
                .build();
        
        cart.addItem(item1);
        cart.addItem(item2);
        
        assertEquals(2, cart.getItems().size());
        assertEquals(19.0, cart.getTotalAmount()); // (5*2) + (3*3) = 10 + 9 = 19
        assertEquals(5, cart.getTotalItems()); // 2 + 3 = 5
    }
}