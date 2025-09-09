package co.edu.uniajc.estudiante.opemayfruitshop.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.ProductRepository;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> getAll() throws ExecutionException, InterruptedException {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return repository.findById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product product) throws ExecutionException, InterruptedException {
        product.setId(UUID.randomUUID().toString());
        return repository.save(product);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) throws ExecutionException, InterruptedException {
        return repository.delete(id);
    }
}
