package co.edu.uniajc.estudiante.opemayfruitshop.service;

import co.edu.uniajc.estudiante.opemayfruitshop.model.CartItem;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CartService {

    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public List<CartItem> getCart(String userId) throws ExecutionException, InterruptedException {
        return repository.getItems(userId);
    }

    public CartItem addItem(String userId, CartItem item) throws ExecutionException, InterruptedException {
        return repository.addItem(userId, item);
    }

    public void removeItem(String userId, String itemId) throws ExecutionException, InterruptedException {
        repository.removeItem(userId, itemId);
    }

    public void clearCart(String userId) throws ExecutionException, InterruptedException {
        repository.clearCart(userId);
    }
}
