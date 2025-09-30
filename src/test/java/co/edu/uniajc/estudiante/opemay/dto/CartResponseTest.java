package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.model.CartItem;

class CartResponseTest {

    private CartResponse response;
    private List<CartItem> items;
    private Timestamp now;

    @BeforeEach
    void setUp() {
        now = Timestamp.now();
        
        CartItem item1 = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(3)
                .build();
        
        CartItem item2 = CartItem.builder()
                .productId("product-2")
                .productName("Banana")
                .price(1.20)
                .quantity(5)
                .build();
        
        items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        
        response = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .items(items)
                .totalAmount(13.50)
                .totalItems(8)
                .status("ACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();
    }

    @Test
    void testCartResponseBuilder() {
        assertNotNull(response);
        assertEquals("cart-1", response.getId());
        assertEquals("user-1", response.getUserId());
        assertEquals(2, response.getItems().size());
        assertEquals(13.50, response.getTotalAmount(), 0.01);
        assertEquals(8, response.getTotalItems());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
        assertTrue(response.getActive());
    }

    @Test
    void testNoArgsConstructor() {
        CartResponse emptyResponse = new CartResponse();
        assertNotNull(emptyResponse);
    }

    @Test
    void testAllArgsConstructor() {
        CartResponse newResponse = new CartResponse(
                "cart-2", "user-2", items, 20.0, 10, 
                "COMPLETED", now, now, false
        );
        
        assertEquals("cart-2", newResponse.getId());
        assertEquals("user-2", newResponse.getUserId());
        assertEquals(2, newResponse.getItems().size());
        assertEquals(20.0, newResponse.getTotalAmount());
        assertEquals(10, newResponse.getTotalItems());
        assertEquals("COMPLETED", newResponse.getStatus());
        assertEquals(false, newResponse.getActive());
    }

    @Test
    void testSettersAndGetters() {
        CartResponse testResponse = new CartResponse();
        
        testResponse.setId("test-cart");
        testResponse.setUserId("test-user");
        testResponse.setItems(items);
        testResponse.setTotalAmount(100.0);
        testResponse.setTotalItems(50);
        testResponse.setStatus("ABANDONED");
        testResponse.setCreatedAt(now);
        testResponse.setUpdatedAt(now);
        testResponse.setActive(false);
        
        assertEquals("test-cart", testResponse.getId());
        assertEquals("test-user", testResponse.getUserId());
        assertEquals(2, testResponse.getItems().size());
        assertEquals(100.0, testResponse.getTotalAmount());
        assertEquals(50, testResponse.getTotalItems());
        assertEquals("ABANDONED", testResponse.getStatus());
        assertEquals(now, testResponse.getCreatedAt());
        assertEquals(now, testResponse.getUpdatedAt());
        assertEquals(false, testResponse.getActive());
    }

    @Test
    void testToString() {
        String responseString = response.toString();
        
        assertThat(responseString).contains("CartResponse");
        assertThat(responseString).contains("cart-1");
        assertThat(responseString).contains("user-1");
        assertThat(responseString).contains("ACTIVE");
    }

    @Test
    void testWithEmptyItems() {
        CartResponse emptyResponse = CartResponse.builder()
                .id("empty-cart")
                .userId("user-1")
                .items(new ArrayList<>())
                .totalAmount(0.0)
                .totalItems(0)
                .status("ACTIVE")
                .active(true)
                .build();
        
        assertNotNull(emptyResponse.getItems());
        assertTrue(emptyResponse.getItems().isEmpty());
        assertEquals(0.0, emptyResponse.getTotalAmount());
        assertEquals(0, emptyResponse.getTotalItems());
    }

    @Test
    void testWithNullFields() {
        CartResponse nullResponse = CartResponse.builder()
                .id(null)
                .userId(null)
                .items(null)
                .totalAmount(null)
                .totalItems(null)
                .status(null)
                .createdAt(null)
                .updatedAt(null)
                .active(null)
                .build();
        
        assertNotNull(nullResponse); // El objeto debe existir aunque tenga campos null
    }
}