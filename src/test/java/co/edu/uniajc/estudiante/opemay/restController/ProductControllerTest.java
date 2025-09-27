package co.edu.uniajc.estudiante.opemay.restController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniajc.estudiante.opemay.Service.ProductService;
import co.edu.uniajc.estudiante.opemay.config.TestFirebaseConfig;
import co.edu.uniajc.estudiante.opemay.config.TestSecurityConfig;
import co.edu.uniajc.estudiante.opemay.model.Product;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@Import({TestFirebaseConfig.class, TestSecurityConfig.class})
@SuppressWarnings("deprecation") // Suppress MockBean deprecation warnings
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean  
    private co.edu.uniajc.estudiante.opemay.Service.JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("product-123")
                .name("Test Product")
                .price(10.50)
                .description("Test product description")
                .categoryId("cat-1")
                .categoryName("Test Category")
                .stock(100)
                .active(true)
                .build();

        Product product2 = Product.builder()
                .id("product-456")
                .name("Another Product")
                .price(25.99)
                .description("Another product description")
                .categoryId("cat-1")
                .categoryName("Test Category")
                .stock(50)
                .active(true)
                .build();

        testProducts = Arrays.asList(testProduct, product2);
    }

    @Test
    @WithMockUser
    void testSaveProduct_Success() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("product-123"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.description").value("Test product description"))
                .andExpect(jsonPath("$.categoryId").value("cat-1"))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    @WithMockUser
    void testSaveProduct_ServiceException() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/products/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isInternalServerError());

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    @WithMockUser
    void testGetAllProducts_Success() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(testProducts);

        // Act & Assert
        mockMvc.perform(get("/api/products/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("product-123"))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[1].id").value("product-456"))
                .andExpect(jsonPath("$[1].name").value("Another Product"));

        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser
    void testGetAllProducts_ServiceException() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/products/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser
    void testGetProductById_Success() throws Exception {
        // Arrange
        when(productService.getProductById("product-123")).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(get("/api/products/product-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-123"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.50));

        verify(productService).getProductById("product-123");
    }

    @Test
    @WithMockUser
    void testGetProductById_NotFound() throws Exception {
        // Arrange
        when(productService.getProductById("nonexistent")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/products/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService).getProductById("nonexistent");
    }

    @Test
    @WithMockUser
    void testGetProductById_ServiceException() throws Exception {
        // Arrange
        when(productService.getProductById("product-123")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/products/product-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(productService).getProductById("product-123");
    }

    @Test
    @WithMockUser
    void testGetProductsByCategory_Success() throws Exception {
        // Arrange
        when(productService.getProductsByCategory("cat-1")).thenReturn(testProducts);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/cat-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoryId").value("cat-1"))
                .andExpect(jsonPath("$[1].categoryId").value("cat-1"));

        verify(productService).getProductsByCategory("cat-1");
    }

    @Test
    @WithMockUser
    void testGetProductsByCategory_EmptyList() throws Exception {
        // Arrange
        when(productService.getProductsByCategory("empty-cat")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/products/category/empty-cat")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getProductsByCategory("empty-cat");
    }

    @Test
    @WithMockUser
    void testGetProductsByCategory_ServiceException() throws Exception {
        // Arrange
        when(productService.getProductsByCategory("cat-1")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/products/category/cat-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(productService).getProductsByCategory("cat-1");
    }

    @Test
    void testControllerExists() {
        ProductController controller = new ProductController(productService);
        assertNotNull(controller);
    }

    @Test
    @WithMockUser
    void testSaveProduct_InvalidInput() throws Exception {
        // Test con producto inválido (sin nombre)
        Product invalidProduct = Product.builder()
                .price(10.50)
                .description("No name")
                .build();

        mockMvc.perform(post("/api/products/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetProducts_ContentType() throws Exception {
        when(productService.getAllProducts()).thenReturn(testProducts);

        mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}