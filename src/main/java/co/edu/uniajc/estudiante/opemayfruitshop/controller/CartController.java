package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import co.edu.uniajc.estudiante.opemayfruitshop.model.CartItem;
import co.edu.uniajc.estudiante.opemayfruitshop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartItem> addItem(@PathVariable String userId, @RequestBody CartItem item) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.addItem(userId, item));
    }

    @DeleteMapping("/{userId}/remove/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable String userId, @PathVariable String itemId) throws ExecutionException, InterruptedException {
        service.removeItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) throws ExecutionException, InterruptedException {
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
