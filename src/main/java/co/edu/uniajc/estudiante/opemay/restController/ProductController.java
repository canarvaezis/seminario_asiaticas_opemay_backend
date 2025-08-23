package co.edu.uniajc.estudiante.opemay.restController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniajc.estudiante.opemay.Service.ProductService;
import co.edu.uniajc.estudiante.opemay.model.Product;

@RestController
@RequestMapping("/products")
public class ProductController {

       private final ProductService productService;
       public ProductController(ProductService productService) {
            this.productService = productService;
       }
        @PostMapping(path = "/save")
    public Product saveProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }
}
