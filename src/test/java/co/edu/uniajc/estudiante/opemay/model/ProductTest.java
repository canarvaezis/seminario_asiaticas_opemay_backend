package co.edu.uniajc.estudiante.opemay.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;

/**
 * Tests unitarios para el modelo Product
 */
@DisplayName("Product Model Tests")
class ProductTest {

    @Test
    @DisplayName("Debe crear producto con builder")
    void shouldCreateProductWithBuilder() {
        // Act
        Product product = Product.builder()
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

        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo("test-id");
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("Test Description");
        assertThat(product.getPrice()).isEqualTo(99.99);
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCategory()).isEqualTo("Electronics");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getImageUrl()).isEqualTo("http://test.com/image.jpg");
        assertThat(product.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe permitir valores null opcionales")
    void shouldAllowNullOptionalValues() {
        // Act
        Product product = Product.builder()
                .id("test-id")
                .name("Test Product")
                .price(99.99)
                .description(null)
                .category(null)
                .imageUrl(null)
                .updatedAt(null)
                .build();

        // Assert
        assertThat(product.getDescription()).isNull();
        assertThat(product.getCategory()).isNull();
        assertThat(product.getImageUrl()).isNull();
        assertThat(product.getUpdatedAt()).isNull();
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getPrice()).isEqualTo(99.99);
    }

    @Test
    @DisplayName("Debe permitir modificar propiedades")
    void shouldAllowModifyingProperties() {
        // Arrange
        Product product = Product.builder()
                .name("Original Product")
                .price(99.99)
                .build();

        // Act
        product.setName("Updated Product");
        product.setPrice(149.99);
        product.setActive(false);
        product.setStock(5);
        product.setUpdatedAt(Timestamp.now());

        // Assert
        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getPrice()).isEqualTo(149.99);
        assertThat(product.getActive()).isFalse();
        assertThat(product.getStock()).isEqualTo(5);
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe manejar precio cero")
    void shouldHandleZeroPrice() {
        // Act
        Product product = Product.builder()
                .name("Free Product")
                .price(0.0)
                .build();

        // Assert
        assertThat(product.getPrice()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debe manejar stock negativo")
    void shouldHandleNegativeStock() {
        // Act
        Product product = Product.builder()
                .name("Out of Stock Product")
                .stock(-1)
                .build();

        // Assert
        assertThat(product.getStock()).isEqualTo(-1);
    }

    @Test
    @DisplayName("Equals y HashCode deben funcionar correctamente")
    void equalsAndHashCodeShouldWorkCorrectly() {
        // Arrange
        Product product1 = Product.builder()
                .id("test-id")
                .name("Test Product")
                .price(99.99)
                .build();

        Product product2 = Product.builder()
                .id("test-id")
                .name("Test Product")
                .price(99.99)
                .build();

        Product product3 = Product.builder()
                .id("different-id")
                .name("Test Product")
                .price(99.99)
                .build();

        // Assert
        assertThat(product1).isEqualTo(product2);
        assertThat(product1).isNotEqualTo(product3);
        assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        assertThat(product1.hashCode()).isNotEqualTo(product3.hashCode());
    }

    @Test
    @DisplayName("ToString debe incluir propiedades principales")
    void toStringShouldIncludeMainProperties() {
        // Arrange
        Product product = Product.builder()
                .id("test-id")
                .name("Test Product")
                .price(99.99)
                .active(true)
                .build();

        // Act
        String productString = product.toString();

        // Assert
        assertThat(productString).contains("test-id");
        assertThat(productString).contains("Test Product");
        assertThat(productString).contains("99.99");
        assertThat(productString).contains("true");
    }

    @Test
    @DisplayName("Debe crear producto mínimo válido")
    void shouldCreateMinimalValidProduct() {
        // Act
        Product product = Product.builder()
                .name("Minimal Product")
                .build();

        // Assert
        assertThat(product.getName()).isEqualTo("Minimal Product");
        assertThat(product.getId()).isNull();
        assertThat(product.getPrice()).isNull();
        assertThat(product.getActive()).isTrue(); // Default value
    }
}