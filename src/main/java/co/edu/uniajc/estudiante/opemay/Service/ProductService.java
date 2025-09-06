package co.edu.uniajc.estudiante.opemay.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import co.edu.uniajc.estudiante.opemay.model.Product;
import co.edu.uniajc.estudiante.opemay.respository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al guardar producto", e);
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al obtener productos", e);
        }
    }

    public Product getProductById(String id) {
        try {
            return productRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al obtener producto con id " + id, e);
        }
    }

    public Product updateProduct(String id, Product updatedProduct) {
        try {
            return productRepository.update(id, updatedProduct);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    public String deleteProduct(String id) {
        try {
            productRepository.delete(id);
            return "Producto eliminado con id: " + id;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }
}
