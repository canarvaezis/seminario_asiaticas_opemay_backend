package co.edu.uniajc.estudiante.opemayfruitshop.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import co.edu.uniajc.estudiante.opemayfruitshop.model.Product;
import co.edu.uniajc.estudiante.opemayfruitshop.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        return repository.findAll();
    }

    public Optional<Product> findById(String id) throws ExecutionException, InterruptedException {
        return repository.findById(id);
    }

    public Product save(Product product) throws ExecutionException, InterruptedException {
        return repository.save(product);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        repository.delete(id);
    }
}
