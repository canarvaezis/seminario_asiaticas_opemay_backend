package co.edu.uniajc.estudiante.opemay.Service;

import com.google.cloud.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.CartRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.OrderRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.ProductRepository;
import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.model.CartItem;
import co.edu.uniajc.estudiante.opemay.model.Order;
import co.edu.uniajc.estudiante.opemay.model.OrderItem;
import co.edu.uniajc.estudiante.opemay.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Crea una orden desde un carrito
     */
    public Order createOrderFromCart(String cartId, String deliveryAddress, String paymentMethod) 
            throws ExecutionException, InterruptedException {
        
        log.info("Creando orden desde carrito ID: {}", cartId);
        
        // Obtener el carrito
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null || !cart.getActive()) {
            throw new IllegalArgumentException("Carrito no encontrado o inactivo");
        }

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Validar stock de todos los productos
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.getProductById(cartItem.getProductId());
            if (product == null || !product.getActive()) {
                throw new IllegalArgumentException("Producto no encontrado o inactivo: " + cartItem.getProductId());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + cartItem.getProductName());
            }
        }

        // Crear la orden
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(cart.getUserId());
        order.setStatus("PENDING");
        order.setCreatedAt(Timestamp.now());
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PENDING");
        order.setActive(true);

        // Convertir items del carrito a items de orden
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.getProductById(cartItem.getProductId());
            
            OrderItem orderItem = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(order.getId())
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .imageUrl(product.getImageUrl())
                    .build();
            
            order.getItems().add(orderItem);
        }

        // Calcular totales
        order.calculateTotals();

        // Guardar la orden
        orderRepository.save(order);

        // Actualizar stock de productos y desactivar carrito
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.getProductById(cartItem.getProductId());
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.update(product);
        }

        // Marcar carrito como completado
        cart.setStatus("COMPLETED");
        cart.setUpdatedAt(Timestamp.now());
        cartRepository.update(cart);

        log.info("Orden creada exitosamente con ID: {}", order.getId());
        return order;
    }

    /**
     * Obtiene una orden por su ID
     */
    public Order getOrderById(String orderId) throws ExecutionException, InterruptedException {
        return orderRepository.getOrderById(orderId);
    }

    /**
     * Obtiene todas las órdenes de un usuario
     */
    public List<Order> getUserOrders(String userId) throws ExecutionException, InterruptedException {
        return orderRepository.getOrdersByUserId(userId);
    }

    /**
     * Obtiene todas las órdenes (para administradores)
     */
    public List<Order> getAllOrders() throws ExecutionException, InterruptedException {
        return orderRepository.getAllOrders();
    }

    /**
     * Obtiene órdenes por estado
     */
    public List<Order> getOrdersByStatus(String status) throws ExecutionException, InterruptedException {
        return orderRepository.getOrdersByStatus(status);
    }

    /**
     * Actualiza el estado de una orden
     */
    public Order updateOrderStatus(String orderId, String newStatus) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando estado de orden {} a {}", orderId, newStatus);
        
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        // Validar transición de estado
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Transición de estado no válida: " + 
                order.getStatus() + " -> " + newStatus);
        }

        order.updateStatus(newStatus);
        
        // Actualizar timestamps según el estado
        switch (newStatus) {
            case "CONFIRMED":
                order.setConfirmedAt(Timestamp.now());
                break;
            case "PROCESSING":
                order.setProcessingAt(Timestamp.now());
                break;
            case "SHIPPED":
                order.setShippedAt(Timestamp.now());
                break;
            case "DELIVERED":
                order.setDeliveredAt(Timestamp.now());
                order.setPaymentStatus("COMPLETED");
                break;
            case "CANCELLED":
                order.setCancelledAt(Timestamp.now());
                // Restaurar stock si la orden se cancela antes de enviar
                if (List.of("PENDING", "CONFIRMED", "PROCESSING").contains(order.getStatus())) {
                    restoreStock(order);
                }
                break;
        }

        orderRepository.update(order);
        log.info("Estado de orden {} actualizado a {}", orderId, newStatus);
        return order;
    }

    /**
     * Cancela una orden
     */
    public Order cancelOrder(String orderId, String reason) 
            throws ExecutionException, InterruptedException {
        
        log.info("Cancelando orden: {}", orderId);
        
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        if (!order.canBeCancelled()) {
            throw new IllegalStateException("La orden no puede ser cancelada en su estado actual: " + 
                order.getStatus());
        }

        order.updateStatus("CANCELLED");
        order.setCancelledAt(Timestamp.now());
        order.setCancelReason(reason);

        // Restaurar stock
        restoreStock(order);

        orderRepository.update(order);
        log.info("Orden {} cancelada exitosamente", orderId);
        return order;
    }

    /**
     * Actualiza el estado de pago de una orden
     */
    public Order updatePaymentStatus(String orderId, String paymentStatus) 
            throws ExecutionException, InterruptedException {
        
        log.info("Actualizando estado de pago de orden {} a {}", orderId, paymentStatus);
        
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Orden no encontrada");
        }

        order.setPaymentStatus(paymentStatus);
        if ("COMPLETED".equals(paymentStatus)) {
            order.setPaidAt(Timestamp.now());
        }

        orderRepository.update(order);
        return order;
    }

    /**
     * Obtiene estadísticas de órdenes
     */
    public OrderStats getOrderStats() throws ExecutionException, InterruptedException {
        List<Order> allOrders = orderRepository.getAllOrders();
        
        OrderStats stats = new OrderStats();
        stats.setTotalOrders(allOrders.size());
        
        for (Order order : allOrders) {
            switch (order.getStatus()) {
                case "PENDING":
                    stats.setPendingOrders(stats.getPendingOrders() + 1);
                    break;
                case "CONFIRMED":
                    stats.setConfirmedOrders(stats.getConfirmedOrders() + 1);
                    break;
                case "PROCESSING":
                    stats.setProcessingOrders(stats.getProcessingOrders() + 1);
                    break;
                case "SHIPPED":
                    stats.setShippedOrders(stats.getShippedOrders() + 1);
                    break;
                case "DELIVERED":
                    stats.setDeliveredOrders(stats.getDeliveredOrders() + 1);
                    stats.setTotalRevenue(stats.getTotalRevenue() + order.getTotalAmount());
                    break;
                case "CANCELLED":
                    stats.setCancelledOrders(stats.getCancelledOrders() + 1);
                    break;
            }
        }
        
        return stats;
    }

    /**
     * Valida si una transición de estado es válida
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return false;
        }

        switch (currentStatus) {
            case "PENDING":
                return List.of("CONFIRMED", "CANCELLED").contains(newStatus);
            case "CONFIRMED":
                return List.of("PROCESSING", "CANCELLED").contains(newStatus);
            case "PROCESSING":
                return List.of("SHIPPED", "CANCELLED").contains(newStatus);
            case "SHIPPED":
                return List.of("DELIVERED", "CANCELLED").contains(newStatus);
            case "DELIVERED":
            case "CANCELLED":
                return false; // Estados finales
            default:
                return false;
        }
    }

    /**
     * Restaura el stock de productos cuando se cancela una orden
     */
    private void restoreStock(Order order) throws ExecutionException, InterruptedException {
        log.info("Restaurando stock para orden cancelada: {}", order.getId());
        
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.getProductById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.update(product);
            }
        }
    }

    /**
     * Clase para estadísticas de órdenes
     */
    public static class OrderStats {
        private int totalOrders;
        private int pendingOrders;
        private int confirmedOrders;
        private int processingOrders;
        private int shippedOrders;
        private int deliveredOrders;
        private int cancelledOrders;
        private double totalRevenue;

        // Getters y Setters
        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getPendingOrders() {
            return pendingOrders;
        }

        public void setPendingOrders(int pendingOrders) {
            this.pendingOrders = pendingOrders;
        }

        public int getConfirmedOrders() {
            return confirmedOrders;
        }

        public void setConfirmedOrders(int confirmedOrders) {
            this.confirmedOrders = confirmedOrders;
        }

        public int getProcessingOrders() {
            return processingOrders;
        }

        public void setProcessingOrders(int processingOrders) {
            this.processingOrders = processingOrders;
        }

        public int getShippedOrders() {
            return shippedOrders;
        }

        public void setShippedOrders(int shippedOrders) {
            this.shippedOrders = shippedOrders;
        }

        public int getDeliveredOrders() {
            return deliveredOrders;
        }

        public void setDeliveredOrders(int deliveredOrders) {
            this.deliveredOrders = deliveredOrders;
        }

        public int getCancelledOrders() {
            return cancelledOrders;
        }

        public void setCancelledOrders(int cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }
}