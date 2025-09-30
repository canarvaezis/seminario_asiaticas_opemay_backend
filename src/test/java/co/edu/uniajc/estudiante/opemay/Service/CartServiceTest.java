package co.edu.uniajc.estudiante.opemay.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uniajc.estudiante.opemay.IRespository.CartRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductRepository;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;
import co.edu.uniajc.estudiante.opemay.model.Product;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .status("ACTIVE")
                .build();

        testProduct = Product.builder()
                .id("product-1")
                .name("Manzana")
                .price(2.50)
                .active(true)
                .imageUrl("http://example.com/image.jpg")
                .build();

        testCartItem = CartItem.builder()
                .productId("product-1")
                .productName("Manzana")
                .price(2.50)
                .quantity(3)
                .build();
    }

    @Test
    void testGetOrCreateActiveCartWhenExists() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);

        Cart result = cartService.getOrCreateActiveCart("user-1");

        assertNotNull(result);
        assertEquals("cart-1", result.getId());
        assertEquals("user-1", result.getUserId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testGetOrCreateActiveCartWhenNotExists() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.getOrCreateActiveCart("user-1");

        assertNotNull(result);
        assertEquals("user-1", result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetOrCreateActiveCartWithNullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.getOrCreateActiveCart(null));

        assertEquals("El ID del usuario no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetOrCreateActiveCartWithEmptyUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.getOrCreateActiveCart(""));

        assertEquals("El ID del usuario no puede estar vacío", exception.getMessage());
    }

    @Test
    void testAddProductToCartSuccess() throws ExecutionException, InterruptedException {
        when(productRepository.getProductById("product-1")).thenReturn(testProduct);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.addProductToCart("user-1", "product-1", 3);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("product-1", result.getItems().get(0).getProductId());
        assertEquals(3, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testAddProductToCartWithInvalidQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.addProductToCart("user-1", "product-1", 0));

        assertEquals("La cantidad debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void testAddProductToCartWithNullQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.addProductToCart("user-1", "product-1", null));

        assertEquals("La cantidad debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void testAddProductToCartProductNotFound() throws ExecutionException, InterruptedException {
        when(productRepository.getProductById("product-1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.addProductToCart("user-1", "product-1", 3));

        assertEquals("Producto no encontrado", exception.getMessage());
    }

    @Test
    void testAddProductToCartProductNotActive() throws ExecutionException, InterruptedException {
        testProduct.setActive(false);
        when(productRepository.getProductById("product-1")).thenReturn(testProduct);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.addProductToCart("user-1", "product-1", 3));

        assertEquals("El producto no está disponible", exception.getMessage());
    }

    @Test
    void testUpdateProductQuantitySuccess() throws ExecutionException, InterruptedException {
        testCart.addItem(testCartItem);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.updateProductQuantity("user-1", "product-1", 5);

        assertNotNull(result);
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testUpdateProductQuantityToZeroRemovesItem() throws ExecutionException, InterruptedException {
        testCart.addItem(testCartItem);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.updateProductQuantity("user-1", "product-1", 0);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testUpdateProductQuantityWithNegativeQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.updateProductQuantity("user-1", "product-1", -1));

        assertEquals("La cantidad no puede ser negativa", exception.getMessage());
    }

    @Test
    void testUpdateProductQuantityProductNotInCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.updateProductQuantity("user-1", "product-1", 5));

        assertEquals("Producto no encontrado en el carrito", exception.getMessage());
    }

    @Test
    void testRemoveProductFromCart() throws ExecutionException, InterruptedException {
        testCart.addItem(testCartItem);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.removeProductFromCart("user-1", "product-1");

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testClearCart() throws ExecutionException, InterruptedException {
        testCart.addItem(testCartItem);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.clearCart("user-1");

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotalAmount());
        assertEquals(0, result.getTotalItems());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testGetActiveCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);

        Cart result = cartService.getActiveCart("user-1");

        assertNotNull(result);
        assertEquals("cart-1", result.getId());
    }

    @Test
    void testGetUserCarts() throws ExecutionException, InterruptedException {
        List<Cart> cartList = List.of(testCart);
        when(cartRepository.getCartsByUserId("user-1")).thenReturn(cartList);

        List<Cart> result = cartService.getUserCarts("user-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cart-1", result.get(0).getId());
    }

    @Test
    void testGetUserCartsWithEmptyUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.getUserCarts(""));

        assertEquals("El ID del usuario no puede estar vacío", exception.getMessage());
    }

    @Test
    void testCompleteCartSuccess() throws ExecutionException, InterruptedException {
        testCart.addItem(testCartItem);
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.completeCart("user-1");

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testCompleteCartNoActiveCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.completeCart("user-1"));

        assertEquals("No hay carrito activo para completar", exception.getMessage());
    }

    @Test
    void testCompleteCartEmptyCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.completeCart("user-1"));

        assertEquals("El carrito está vacío", exception.getMessage());
    }

    @Test
    void testAbandonCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(testCart);
        when(cartRepository.update(any(Cart.class))).thenReturn("timestamp");

        Cart result = cartService.abandonCart("user-1");

        assertNotNull(result);
        assertEquals("ABANDONED", result.getStatus());
        assertEquals(false, result.getActive());
        verify(cartRepository, times(1)).update(any(Cart.class));
    }

    @Test
    void testAbandonCartNoActiveCart() throws ExecutionException, InterruptedException {
        when(cartRepository.getActiveCartByUserId("user-1")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.abandonCart("user-1"));

        assertEquals("No hay carrito activo para abandonar", exception.getMessage());
    }

    @Test
    void testGetCartById() throws ExecutionException, InterruptedException {
        when(cartRepository.getCartById("cart-1")).thenReturn(testCart);

        Cart result = cartService.getCartById("cart-1");

        assertNotNull(result);
        assertEquals("cart-1", result.getId());
    }

    @Test
    void testGetCartByIdWithEmptyId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.getCartById(""));

        assertEquals("El ID del carrito no puede estar vacío", exception.getMessage());
    }

    @Test
    void testGetAllCarts() throws ExecutionException, InterruptedException {
        List<Cart> cartList = List.of(testCart);
        when(cartRepository.getAllCarts()).thenReturn(cartList);

        List<Cart> result = cartService.getAllCarts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCartsByStatus() throws ExecutionException, InterruptedException {
        List<Cart> cartList = List.of(testCart);
        when(cartRepository.getCartsByStatus("ACTIVE")).thenReturn(cartList);

        List<Cart> result = cartService.getCartsByStatus("ACTIVE");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
    }

    @Test
    void testGetCartsByStatusWithEmptyStatus() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.getCartsByStatus(""));

        assertEquals("El estado no puede estar vacío", exception.getMessage());
    }
}