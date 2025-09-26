package co.edu.uniajc.estudiante.opemay.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddToCartRequestTest {

    private Validator validator;
    private AddToCartRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        request = AddToCartRequest.builder()
                .productId("product-1")
                .quantity(3)
                .build();
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }

    @Test
    void testRequestBuilder() {
        assertNotNull(request);
        assertEquals("product-1", request.getProductId());
        assertEquals(3, request.getQuantity());
    }

    @Test
    void testNullProductId() {
        request.setProductId(null);
        
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El ID del producto es requerido");
    }

    @Test
    void testBlankProductId() {
        request.setProductId("");
        
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("El ID del producto es requerido");
    }

    @Test
    void testNullQuantity() {
        request.setQuantity(null);
        
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La cantidad es requerida");
    }

    @Test
    void testZeroQuantity() {
        request.setQuantity(0);
        
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La cantidad debe ser mayor a 0");
    }

    @Test
    void testNegativeQuantity() {
        request.setQuantity(-1);
        
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La cantidad debe ser mayor a 0");
    }

    @Test
    void testNoArgsConstructor() {
        AddToCartRequest emptyRequest = new AddToCartRequest();
        assertNotNull(emptyRequest);
    }

    @Test
    void testAllArgsConstructor() {
        AddToCartRequest newRequest = new AddToCartRequest("product-2", 5);
        
        assertEquals("product-2", newRequest.getProductId());
        assertEquals(5, newRequest.getQuantity());
    }

    @Test
    void testToString() {
        String requestString = request.toString();
        
        assertThat(requestString).contains("AddToCartRequest");
        assertThat(requestString).contains("product-1");
        assertThat(requestString).contains("3");
    }
}