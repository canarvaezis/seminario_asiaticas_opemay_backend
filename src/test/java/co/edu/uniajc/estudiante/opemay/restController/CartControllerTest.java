package co.edu.uniajc.estudiante.opemay.restController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniajc.estudiante.opemay.Service.CartService;
import co.edu.uniajc.estudiante.opemay.dto.AddToCartRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdateCartItemRequest;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;

@WebMvcTest(CartController.class)
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "USER")
    void testGetActiveCart() throws Exception {
        when(cartService.getActiveCart(anyString())).thenReturn(testCart);

        mockMvc.perform(get("/api/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cart-1"))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value("product-1"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddToCart() throws Exception {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId("product-1")
                .quantity(3)
                .build();

        when(cartService.addProductToCart(anyString(), anyString(), any(Integer.class)))
                .thenReturn(testCart);

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cart-1"))
                .andExpect(jsonPath("$.items[0].productId").value("product-1"))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddToCartWithInvalidRequest() throws Exception {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId("")
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER") 
    void testUpdateCartItem() throws Exception {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .productId("product-1")
                .quantity(5)
                .build();

        testCartItem.setQuantity(5);
        when(cartService.updateProductQuantity(anyString(), anyString(), any(Integer.class)))
                .thenReturn(testCart);

        mockMvc.perform(put("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRemoveFromCart() throws Exception {
        Cart emptyCart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .status("ACTIVE")
                .build();

        when(cartService.removeProductFromCart(anyString(), anyString()))
                .thenReturn(emptyCart);

        mockMvc.perform(delete("/api/cart/items/product-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testClearCart() throws Exception {
        Cart emptyCart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .status("ACTIVE")
                .build();

        when(cartService.clearCart(anyString())).thenReturn(emptyCart);

        mockMvc.perform(delete("/api/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0.0))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCompleteCart() throws Exception {
        testCart.setStatus("COMPLETED");
        when(cartService.completeCart(anyString())).thenReturn(testCart);

        mockMvc.perform(post("/api/cart/complete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetCartHistory() throws Exception {
        List<Cart> cartList = List.of(testCart);
        when(cartService.getUserCarts(anyString())).thenReturn(cartList);

        mockMvc.perform(get("/api/cart/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("cart-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllCartsAsAdmin() throws Exception {
        List<Cart> cartList = List.of(testCart);
        when(cartService.getAllCarts()).thenReturn(cartList);

        mockMvc.perform(get("/api/cart/admin/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("cart-1"));
    }

    @Test
    void testGetActiveCartWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}