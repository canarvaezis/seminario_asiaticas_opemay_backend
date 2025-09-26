package co.edu.uniajc.estudiante.opemay.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemTest {

    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cartItem = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(3)
                .imageUrl("http://example.com/image.jpg")
                .build();
    }

    @Test
    void testCartItemBuilder() {
        assertNotNull(cartItem);
        assertEquals("product-1", cartItem.getProductId());
        assertEquals("Manzana", cartItem.getProductName());
        assertEquals(2.50, cartItem.getPrice());
        assertEquals(3, cartItem.getQuantity());
        assertEquals("http://example.com/image.jpg", cartItem.getImageUrl());
        assertNotNull(cartItem.getCreatedAt());
    }

    @Test
    void testGetSubtotal() {
        double expectedSubtotal = 2.50 * 3;
        assertEquals(expectedSubtotal, cartItem.getSubtotal(), 0.01);
    }

    @Test
    void testGetSubtotalWithNullPrice() {
        cartItem.setPrice(null);
        assertEquals(0.0, cartItem.getSubtotal());
    }

    @Test
    void testGetSubtotalWithNullQuantity() {
        cartItem.setQuantity(null);
        assertEquals(0.0, cartItem.getSubtotal());
    }

    @Test
    void testGetSubtotalWithBothNull() {
        cartItem.setPrice(null);
        cartItem.setQuantity(null);
        assertEquals(0.0, cartItem.getSubtotal());
    }

    @Test
    void testIsValidWithValidItem() {
        assertTrue(cartItem.isValid());
    }

    @Test
    void testIsValidWithNullProductId() {
        cartItem.setProductId(null);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithEmptyProductId() {
        cartItem.setProductId("");
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithBlankProductId() {
        cartItem.setProductId("   ");
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithNullPrice() {
        cartItem.setPrice(null);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithZeroPrice() {
        cartItem.setPrice(0.0);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithNegativePrice() {
        cartItem.setPrice(-1.0);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithNullQuantity() {
        cartItem.setQuantity(null);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithZeroQuantity() {
        cartItem.setQuantity(0);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testIsValidWithNegativeQuantity() {
        cartItem.setQuantity(-1);
        assertFalse(cartItem.isValid());
    }

    @Test
    void testEqualsAndHashCode() {
        CartItem item1 = CartItem.builder().productId("product-1").build();
        CartItem item2 = CartItem.builder().productId("product-1").build();
        CartItem item3 = CartItem.builder().productId("product-2").build();
        
        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void testToString() {
        String itemString = cartItem.toString();
        
        assertThat(itemString).contains("CartItem");
        assertThat(itemString).contains("product-1");
        assertThat(itemString).contains("Manzana");
    }

    @Test
    void testNoArgsConstructor() {
        CartItem emptyItem = new CartItem();
        
        assertNotNull(emptyItem);
        assertNotNull(emptyItem.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        CartItem item = new CartItem("product-2", "Banana", 1.20, 5, 
                "http://example.com/banana.jpg", null, null);
        
        assertEquals("product-2", item.getProductId());
        assertEquals("Banana", item.getProductName());
        assertEquals(1.20, item.getPrice());
        assertEquals(5, item.getQuantity());
        assertEquals("http://example.com/banana.jpg", item.getImageUrl());
    }
}