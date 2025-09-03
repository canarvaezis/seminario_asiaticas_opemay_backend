package co.edu.uniajc.estudiante.opemay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.uniajc.estudiante.opemay.model.Cart;
import co.edu.uniajc.estudiante.opemay.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    // POST /cart/{userId}/items
    @PostMapping("/{userId}/items")
    public ResponseEntity<List<Cart>> addItem(@PathVariable String userId, @RequestBody Cart item) {
        try {
            List<Cart> items = service.addItem(userId, item);
            return ResponseEntity.status(HttpStatus.CREATED).body(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /cart/{userId}/items/{productId}?quantity=5
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<List<Cart>> updateQuantity(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestParam int quantity) {
        try {
            List<Cart> items = service.updateItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /cart/{userId}/items/{productId}
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<List<Cart>> removeItem(
            @PathVariable String userId,
            @PathVariable String productId) {
        try {
            List<Cart> items = service.removeItem(userId, productId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /cart/{userId}/items
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<Cart>> getItems(@PathVariable String userId) {
        try {
            List<Cart> items = service.getItems(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /cart/{userId}/total
    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> getTotal(@PathVariable String userId) {
        try {
            double total = service.getTotal(userId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
