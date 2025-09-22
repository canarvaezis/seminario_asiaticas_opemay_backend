package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Order;
import co.edu.uniajc.estudiante.opemayfruitshop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/{userId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.createOrder(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) throws ExecutionException, InterruptedException {
        Order order = service.getOrder(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getAllOrders());
    }
}
