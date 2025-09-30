package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateCartItemRequestTest {

    private Validator validator;
    private UpdateCartItemRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        request = UpdateCartItemRequest.builder()
                .productId("product-1")
                .quantity(5)
                .build();
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidRequestWithZeroQuantity() {
        request.setQuantity(0);
        
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }

    @Test
    void testRequestBuilder() {
        assertNotNull(request);
        assertEquals("product-1", request.getProductId());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testNullProductId() {
        request.setProductId(null);
        
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El ID del producto es requerido");
    }

    @Test
    void testBlankProductId() {
        request.setProductId("");
        
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El ID del producto es requerido");
    }

    @Test
    void testNullQuantity() {
        request.setQuantity(null);
        
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La cantidad es requerida");
    }

    @Test
    void testNegativeQuantity() {
        request.setQuantity(-1);
        
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La cantidad no puede ser negativa");
    }

    @Test
    void testNoArgsConstructor() {
        UpdateCartItemRequest emptyRequest = new UpdateCartItemRequest();
        assertNotNull(emptyRequest);
    }

    @Test
    void testAllArgsConstructor() {
        UpdateCartItemRequest newRequest = new UpdateCartItemRequest("product-2", 0);
        
        assertEquals("product-2", newRequest.getProductId());
        assertEquals(0, newRequest.getQuantity());
    }

    @Test
    void testToString() {
        String requestString = request.toString();
        
        assertThat(requestString).contains("UpdateCartItemRequest");
        assertThat(requestString).contains("product-1");
        assertThat(requestString).contains("5");
    }
}