package co.edu.uniajc.estudiante.opemay.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

class CartTest {

    private Cart cart;
    private CartItem cartItem1;
    private CartItem cartItem2;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .build();

        cartItem1 = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(3)
                .build();

        cartItem2 = CartItem.builder()
                .productId("product-2")
                .productName("Banana")
                .price(1.20)
                .quantity(5)
                .build();
    }

    @Test
    void testCartBuilder() {
        assertNotNull(cart);
        assertEquals("cart-1", cart.getId());
        assertEquals("user-1", cart.getUserId());
        assertEquals("ACTIVE", cart.getStatus());
        assertTrue(cart.getActive());
        assertNotNull(cart.getItems());
        assertEquals(0, cart.getItems().size());
        assertEquals(0.0, cart.getTotalAmount());
        assertEquals(0, cart.getTotalItems());
    }

    @Test
    void testCalculateTotalsWithEmptyCart() {
        cart.calculateTotals();
        
        assertEquals(0.0, cart.getTotalAmount());
        assertEquals(0, cart.getTotalItems());
    }

    @Test
    void testCalculateTotalsWithItems() {
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem1); // 2.50 * 3 = 7.50
        items.add(cartItem2); // 1.20 * 5 = 6.00
        cart.setItems(items);
        
        cart.calculateTotals();
        
        assertEquals(13.50, cart.getTotalAmount(), 0.01);
        assertEquals(8, cart.getTotalItems());
    }

    @Test
    void testAddNewItem() {
        cart.addItem(cartItem1);
        
        assertEquals(1, cart.getItems().size());
        assertEquals(7.50, cart.getTotalAmount(), 0.01);
        assertEquals(3, cart.getTotalItems());
        assertNotNull(cart.getUpdatedAt());
    }

    @Test
    void testAddExistingItemUpdatesQuantity() {
        cart.addItem(cartItem1);
        
        CartItem sameProduct = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(2)
                .build();
        
        cart.addItem(sameProduct);
        
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertEquals(12.50, cart.getTotalAmount(), 0.01);
        assertEquals(5, cart.getTotalItems());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        
        assertEquals(2, cart.getItems().size());
        
        cart.removeItem("product-1");
        
        assertEquals(1, cart.getItems().size());
        assertEquals("product-2", cart.getItems().get(0).getProductId());
        assertEquals(6.00, cart.getTotalAmount(), 0.01);
        assertEquals(5, cart.getTotalItems());
    }

    @Test
    void testRemoveNonExistentItem() {
        cart.addItem(cartItem1);
        int originalSize = cart.getItems().size();
        double originalTotal = cart.getTotalAmount();
        
        cart.removeItem("non-existent-product");
        
        assertEquals(originalSize, cart.getItems().size());
        assertEquals(originalTotal, cart.getTotalAmount(), 0.01);
    }

    @Test
    void testClearCart() {
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);
        
        assertFalse(cart.getItems().isEmpty());
        
        cart.clearCart();
        
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalAmount());
        assertEquals(0, cart.getTotalItems());
        assertNotNull(cart.getUpdatedAt());
    }

    @Test
    void testAddItemWithNullItems() {
        cart.setItems(null);
        
        cart.addItem(cartItem1);
        
        assertNotNull(cart.getItems());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void testClearCartWithNullItems() {
        cart.setItems(null);
        
        cart.clearCart();
        
        // No debe lanzar excepción
        assertEquals(0.0, cart.getTotalAmount());
        assertEquals(0, cart.getTotalItems());
    }

    @Test
    void testEqualsAndHashCode() {
        Cart cart1 = Cart.builder().id("cart-1").build();
        Cart cart2 = Cart.builder().id("cart-1").build();
        Cart cart3 = Cart.builder().id("cart-2").build();
        
        assertThat(cart1).isEqualTo(cart2);
        assertThat(cart1).isNotEqualTo(cart3);
        assertThat(cart1.hashCode()).isEqualTo(cart2.hashCode());
    }

    @Test
    void testToString() {
        String cartString = cart.toString();
        
        assertThat(cartString).contains("Cart");
        assertThat(cartString).contains("cart-1");
        assertThat(cartString).contains("user-1");
    }

    @Test
    void testDefaultValues() {
        Cart newCart = new Cart();
        
        assertNotNull(newCart.getItems());
        assertEquals(0.0, newCart.getTotalAmount());
        assertEquals(0, newCart.getTotalItems());
        assertEquals("ACTIVE", newCart.getStatus());
        assertTrue(newCart.getActive());
        assertNotNull(newCart.getCreatedAt());
    }
}