package co.edu.uniajc.estudiante.opemay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.Timestamp;

import co.edu.uniajc.estudiante.opemay.Service.ProductService;
import co.edu.uniajc.estudiante.opemay.model.Product;
import co.edu.uniajc.estudiante.opemay.restController.ProductController;

/**
 * Tests de integración para ProductController
 */
@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("test-id")
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .active(true)
                .category("Electronics")
                .stock(10)
                .imageUrl("http://test.com/image.jpg")
                .createdAt(Timestamp.now())
                .build();

        Product product2 = Product.builder()
                .id("test-id-2")
                .name("Test Product 2")
                .description("Test Description 2")
                .price(149.99)
                .active(true)
                .category("Books")
                .stock(5)
                .build();

        testProducts = Arrays.asList(testProduct, product2);
    }

    @Nested
    @DisplayName("GET /api/products Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Debe retornar lista de productos exitosamente")
        void shouldReturnProductsListSuccessfully() throws Exception {
            // Arrange
            when(productService.getAllProducts()).thenReturn(testProducts);

            // Act & Assert
            mockMvc.perform(get("/api/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("test-id"))
                    .andExpect(jsonPath("$[0].name").value("Test Product"))
                    .andExpect(jsonPath("$[1].id").value("test-id-2"))
                    .andExpect(jsonPath("$[1].name").value("Test Product 2"));
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay productos")
        void shouldReturnEmptyListWhenNoProducts() throws Exception {
            // Arrange
            when(productService.getAllProducts()).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/api/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Debe manejar errores del servicio")
        void shouldHandleServiceErrors() throws Exception {
            // Arrange
            when(productService.getAllProducts()).thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            mockMvc.perform(get("/api/products"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id} Tests")
    class GetProductByIdTests {

        @Test
        @DisplayName("Debe retornar producto por ID exitosamente")
        void shouldReturnProductByIdSuccessfully() throws Exception {
            // Arrange
            when(productService.getProductById("test-id")).thenReturn(testProduct);

            // Act & Assert
            mockMvc.perform(get("/api/products/test-id"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value("test-id"))
                    .andExpect(jsonPath("$.name").value("Test Product"))
                    .andExpect(jsonPath("$.price").value(99.99));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando producto no existe")
        void shouldReturn404WhenProductNotFound() throws Exception {
            // Arrange
            when(productService.getProductById("non-existent")).thenReturn(null);

            // Act & Assert
            mockMvc.perform(get("/api/products/non-existent"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debe manejar errores del servicio en getById")
        void shouldHandleServiceErrorsInGetById() throws Exception {
            // Arrange
            when(productService.getProductById(anyString())).thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            mockMvc.perform(get("/api/products/test-id"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /api/products Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Debe crear producto exitosamente")
        void shouldCreateProductSuccessfully() throws Exception {
            // Arrange
            Product newProduct = Product.builder()
                    .name("New Product")
                    .description("New Description")
                    .price(199.99)
                    .active(true)
                    .build();

            Product createdProduct = Product.builder()
                    .id("new-id")
                    .name("New Product")
                    .description("New Description")
                    .price(199.99)
                    .active(true)
                    .build();

            when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

            // Act & Assert
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newProduct)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value("new-id"))
                    .andExpect(jsonPath("$.name").value("New Product"))
                    .andExpect(jsonPath("$.price").value(199.99));
        }

        @Test
        @DisplayName("Debe manejar errores del servicio en create")
        void shouldHandleServiceErrorsInCreate() throws Exception {
            // Arrange
            Product newProduct = Product.builder()
                    .name("New Product")
                    .price(199.99)
                    .build();

            when(productService.createProduct(any(Product.class))).thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newProduct)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Debe manejar JSON malformado")
        void shouldHandleMalformedJson() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe manejar content-type incorrecto")
        void shouldHandleWrongContentType() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("plain text"))
                    .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}