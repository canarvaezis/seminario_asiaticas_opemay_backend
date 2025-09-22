package co.edu.uniajc.estudiante.opemayfruitshop.service;

import co.edu.uniajc.estudiante.opemayfruitshop.model.*;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.CartRepository;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.OrderRepository;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(String userId) throws ExecutionException, InterruptedException {
        // obtener items del carrito
        List<CartItem> cartItems = cartRepository.getItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Carrito vacío, no se puede crear pedido.");
        }

        // convertir a OrderItems con snapshot de producto
        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem ci : cartItems) {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + ci.getProductId()));

            OrderItem oi = new OrderItem(product.getId(), product.getName(), product.getPrice(), ci.getQuantity());
            orderItems.add(oi);
            total += product.getPrice() * ci.getQuantity();
        }

        // generar id basado en timestamp
        String orderId = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                .format(Instant.now().atZone(java.time.ZoneId.of("UTC")));

        Order order = new Order(
                orderId,
                userId,
                Instant.now().toString(),
                "PENDING",
                total,
                orderItems
        );

        // guardar en firestore
        orderRepository.save(order);

        // limpiar carrito
        cartRepository.clearCart(userId);

        return order;
    }

    public Order getOrder(String id) throws ExecutionException, InterruptedException {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() throws ExecutionException, InterruptedException {
        return orderRepository.findAll();
    }
}
