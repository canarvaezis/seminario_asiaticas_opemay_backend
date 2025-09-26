package co.edu.uniajc.estudiante.opemay.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateOrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId("cart-123");
        request.setDeliveryAddress("Calle 123 #45-67, Apartamento 890");
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCartIdNotBlank() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId(""); // Vacío
        request.setDeliveryAddress("Calle 123 #45-67, Apartamento 890");
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("El ID del carrito es requerido", violation.getMessage());
    }

    @Test
    void testCartIdNull() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId(null);
        request.setDeliveryAddress("Calle 123 #45-67, Apartamento 890");
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("El ID del carrito es requerido", violation.getMessage());
    }

    @Test
    void testDeliveryAddressNotBlank() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId("cart-123");
        request.setDeliveryAddress(""); // Vacío
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("La dirección de entrega es requerida", violation.getMessage());
    }

    @Test
    void testDeliveryAddressTooShort() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId("cart-123");
        request.setDeliveryAddress("Corta"); // Menos de 10 caracteres
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("La dirección debe tener entre 10 y 200 caracteres", violation.getMessage());
    }

    @Test
    void testDeliveryAddressTooLong() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId("cart-123");
        request.setDeliveryAddress("A".repeat(201)); // Más de 200 caracteres
        request.setPaymentMethod("CREDIT_CARD");

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("La dirección debe tener entre 10 y 200 caracteres", violation.getMessage());
    }

    @Test
    void testPaymentMethodNotBlank() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId("cart-123");
        request.setDeliveryAddress("Calle 123 #45-67, Apartamento 890");
        request.setPaymentMethod(""); // Vacío

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        ConstraintViolation<CreateOrderRequest> violation = violations.iterator().next();
        assertEquals("El método de pago es requerido", violation.getMessage());
    }

    @Test
    void testMultipleValidationErrors() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCartId(""); // Error 1
        request.setDeliveryAddress(""); // Error 2
        request.setPaymentMethod(""); // Error 3

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size());
    }
}