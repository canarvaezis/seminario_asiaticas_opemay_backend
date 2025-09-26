package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import co.edu.uniajc.estudiante.opemay.Service.CartService;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;

@SpringBootTest
@ActiveProfiles("test")
class CartControllerTest {

    @MockBean
    private CartService cartService;

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
    void testCartServiceMocking() throws Exception {
        // Test simple para verificar que el servicio se puede mockear
        when(cartService.getActiveCart(anyString())).thenReturn(testCart);
        
        Cart result = cartService.getActiveCart("user-1");
        
        assertEquals("cart-1", result.getId());
        assertEquals("user-1", result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
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
}