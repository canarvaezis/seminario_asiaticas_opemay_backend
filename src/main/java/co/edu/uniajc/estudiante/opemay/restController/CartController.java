package co.edu.uniajc.estudiante.opemay.restController;

import java.util.List;
import java.util.concurrent.ExecutionException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemay.Service.CartService;
import co.edu.uniajc.estudiante.opemay.dto.AddToCartRequest;
import co.edu.uniajc.estudiante.opemay.dto.CartResponse;
import co.edu.uniajc.estudiante.opemay.dto.UpdateCartItemRequest;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Obtiene el carrito activo del usuario autenticado
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> getActiveCart() {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.getActiveCart(userId);
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener carrito activo", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Agrega un producto al carrito
     */
    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.addProductToCart(userId, request.getProductId(), request.getQuantity());
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al agregar producto al carrito", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al agregar al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @PutMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> updateCartItem(@Valid @RequestBody UpdateCartItemRequest request) {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.updateProductQuantity(userId, request.getProductId(), request.getQuantity());
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al actualizar item del carrito", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al actualizar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remueve un producto del carrito
     */
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable String productId) {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.removeProductFromCart(userId, productId);
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al remover producto del carrito", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al remover del carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Limpia todo el carrito
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> clearCart() {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.clearCart(userId);
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al limpiar carrito", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al limpiar carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Completa el carrito (checkout)
     */
    @PostMapping("/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> completeCart() {
        try {
            String userId = getCurrentUserId();
            Cart cart = cartService.completeCart(userId);
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al completar carrito", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al completar carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene todos los carritos del usuario (historial)
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartResponse>> getCartHistory() {
        try {
            String userId = getCurrentUserId();
            List<Cart> carts = cartService.getUserCarts(userId);
            
            List<CartResponse> responses = carts.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(responses);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener historial de carritos", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener historial: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints administrativos

    /**
     * Obtiene todos los carritos (solo administradores)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        try {
            List<Cart> carts = cartService.getAllCarts();
            
            List<CartResponse> responses = carts.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(responses);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener todos los carritos", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene carritos por estado (solo administradores)
     */
    @GetMapping("/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CartResponse>> getCartsByStatus(@RequestParam String status) {
        try {
            List<Cart> carts = cartService.getCartsByStatus(status);
            
            List<CartResponse> responses = carts.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return ResponseEntity.ok(responses);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener carritos por estado", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener carritos por estado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene un carrito específico por ID (solo administradores)
     */
    @GetMapping("/admin/{cartId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getCartById(@PathVariable String cartId) {
        try {
            Cart cart = cartService.getCartById(cartId);
            
            if (cart == null) {
                return ResponseEntity.notFound().build();
            }
            
            CartResponse response = convertToResponse(cart);
            return ResponseEntity.ok(response);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener carrito por ID", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.warn("Argumento inválido al obtener carrito por ID: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Métodos auxiliares

    /**
     * Obtiene el ID del usuario autenticado
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }

    /**
     * Convierte un Cart a CartResponse
     */
    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems())
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .status(cart.getStatus())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .active(cart.getActive())
                .build();
    }
}