package co.edu.uniajc.estudiante.opemay.restController;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemay.Service.OrderService;
import co.edu.uniajc.estudiante.opemay.Service.UserService;
import co.edu.uniajc.estudiante.opemay.dto.CreateOrderRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdateOrderStatusRequest;
import co.edu.uniajc.estudiante.opemay.dto.UpdatePaymentStatusRequest;
import co.edu.uniajc.estudiante.opemay.model.Order;
import co.edu.uniajc.estudiante.opemay.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Crear una nueva orden desde un carrito
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Principal principal) {
        
        try {
            // ====== LOGGING DETALLADO DE ENTRADA ======
            log.info("🔹 [ORDEN] ===== INICIO CREACIÓN DE ORDEN =====");
            log.info("🔹 [ORDEN] Usuario {} creando orden desde carrito {}", 
                principal.getName(), request.getCartId());
            log.info("🔹 [ORDEN] Request completo: {}", request);
            log.info("🔹 [ORDEN] CartId: '{}'", request.getCartId());
            
            // ====== CREACIÓN DE ORDEN DIRECTA ======
            log.info("🔸 [ORDEN] Iniciando creación de orden directamente desde carrito...");
            log.info("🔸 [ORDEN] El carrito contiene el userId, no necesitamos buscarlo por separado");
            log.info("🔸 [ORDEN] Parámetros: cartId='{}', deliveryAddress='{}', paymentMethod='{}'", 
                    request.getCartId(), request.getDeliveryAddress(), request.getPaymentMethod());
            
            // El orderService.createOrderFromCart obtendrá el userId desde el carrito
            Order order = orderService.createOrderFromCart(
                request.getCartId(),
                request.getDeliveryAddress(),
                request.getPaymentMethod()
            );
            
            // ====== LOGGING RESULTADO ORDEN ======
            if (order != null) {
                log.info("✅ [ORDEN] Orden creada exitosamente:");
                log.info("✅ [ORDEN] ID: {}", order.getId());
                log.info("✅ [ORDEN] Total: {}", order.getTotalAmount());
                log.info("✅ [ORDEN] Status: {}", order.getStatus());
                log.info("✅ [ORDEN] Items count: {}", order.getItems() != null ? order.getItems().size() : "null");
            } else {
                log.error("❌ [ORDEN] La orden creada es null");
            }
            
            log.info("🔹 [ORDEN] ===== FIN CREACIÓN DE ORDEN =====");
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ [ERROR] Error en validación al crear orden: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", "Error: " + e.getMessage()));
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("❌ [ERROR] Error al crear orden", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error interno del servidor"));
        } catch (Exception e) {
            log.error("❌ [ERROR] Error inesperado al crear orden", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error inesperado: " + e.getMessage()));
        }
    }

    /**
     * Obtener orden por ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrder(
            @PathVariable String orderId,
            Principal principal) {
        
        try {
            // Primero validar el usuario autenticado
            User currentUser = userService.getUserByEmail(principal.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Usuario no autenticado"));
            }
            
            Order order = orderService.getOrderById(orderId);
            
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Orden no encontrada"));
            }

            // Los usuarios solo pueden ver sus propias órdenes, los admin pueden ver todas
            if (!currentUser.getRoles().contains("ADMIN") && !order.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", "No autorizado para ver esta orden"));
            }

            return ResponseEntity.ok(order);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener orden {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Obtener órdenes del usuario actual
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        try {
            User currentUser = userService.getUserByEmail(principal.getName());
            List<Order> orders = orderService.getUserOrders(currentUser.getId());
            
            return ResponseEntity.ok(orders);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener órdenes del usuario {}", principal.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    /**
     * Obtener todas las órdenes (solo administradores)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener todas las órdenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    /**
     * Obtener órdenes por estado (solo administradores)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener órdenes por estado {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Actualizar estado de una orden
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        try {
            log.info("Actualizando estado de orden {} a {}", orderId, request.getStatus());
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
            
            if (updatedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Orden no encontrada"));
            }
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error en validación al actualizar estado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", "Error: " + e.getMessage()));
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al actualizar estado de orden {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Cancelar una orden
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false, defaultValue = "Cancelado por el usuario") String reason,
            Principal principal) {
        
        try {
            // Verificar permisos
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            User currentUser = userService.getUserByEmail(principal.getName());
            if (!currentUser.getRoles().contains("ADMIN") && !order.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("No autorizado para cancelar esta orden");
            }

            Order cancelledOrder = orderService.cancelOrder(orderId, reason);
            return ResponseEntity.ok(cancelledOrder);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Error al cancelar orden {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al cancelar orden {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    /**
     * Actualizar estado de pago (solo administradores)
     */
    @PutMapping("/{orderId}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        
        try {
            log.info("Actualizando estado de pago de orden {} a {}", 
                orderId, request.getPaymentStatus());
            
            Order updatedOrder = orderService.updatePaymentStatus(orderId, request.getPaymentStatus());
            
            if (updatedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Orden no encontrada"));
            }
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error al actualizar estado de pago: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("error", "Error: " + e.getMessage()));
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al actualizar estado de pago de orden {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Obtener estadísticas de órdenes (solo administradores)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrderStats() {
        try {
            OrderService.OrderStats stats = orderService.getOrderStats();
            return ResponseEntity.ok(stats);
            
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error al obtener estadísticas de órdenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }
}