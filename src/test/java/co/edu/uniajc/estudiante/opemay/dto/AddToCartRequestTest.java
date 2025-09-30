package co.edu.uniajc.estudiante.opemay.dto;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Pruebas unitarias optimizadas para AddToCartRequest DTO
 * Cumple con estándares de calidad SonarQube
 * 
 * @author OpemAy Team
 * @version 1.0
 * @since 2025-01-26
 */
@DisplayName("AddToCartRequest DTO Tests")
class AddToCartRequestTest {

    private static final String VALID_PRODUCT_ID = "product-1";
    private static final String ANOTHER_PRODUCT_ID = "product-2";
    private static final int VALID_QUANTITY = 3;
    private static final int ANOTHER_VALID_QUANTITY = 5;
    private static final String REQUIRED_PRODUCT_ID_MESSAGE = "El ID del producto es requerido";
    private static final String REQUIRED_QUANTITY_MESSAGE = "La cantidad es requerida";
    private static final String MIN_QUANTITY_MESSAGE = "La cantidad debe ser mayor a 0";

    private Validator validator;
    private ValidatorFactory validatorFactory;
    private AddToCartRequest request;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        
        request = AddToCartRequest.builder()
                .productId(VALID_PRODUCT_ID)
                .quantity(VALID_QUANTITY)
                .build();
    }

    @AfterEach
    void tearDown() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Nested
    @DisplayName("Validaciones Exitosas")
    class ValidacionesExitosas {

        @Test
        @DisplayName("Debe validar request correctamente construido")
        void debeValidarRequestCorrectamenteConstruido() {
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Debe construir objeto con builder pattern")
        void debeConstruirObjetoConBuilderPattern() {
            // Then
            assertThat(request)
                    .isNotNull()
                    .satisfies(r -> {
                        assertThat(r.getProductId()).isEqualTo(VALID_PRODUCT_ID);
                        assertThat(r.getQuantity()).isEqualTo(VALID_QUANTITY);
                    });
        }

        @ParameterizedTest
        @DisplayName("Debe aceptar cantidades válidas")
        @ValueSource(ints = {1, 2, 5, 10, 100, 999})
        void debeAceptarCantidadesValidas(int cantidadValida) {
            // Given
            request.setQuantity(cantidadValida);
            
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Validaciones de ProductId")
    class ValidacionesProductId {

        @Test
        @DisplayName("Debe fallar con productId nulo")
        void debeFallarConProductIdNulo() {
            // Given
            request.setProductId(null);
            
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations)
                    .hasSize(1)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly(REQUIRED_PRODUCT_ID_MESSAGE);
        }

        @ParameterizedTest
        @DisplayName("Debe fallar con productId vacío o solo espacios")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void debeFallarConProductIdVacioOSoloEspacios(String productIdInvalido) {
            // Given
            request.setProductId(productIdInvalido);
            
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations)
                    .hasSize(1)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly(REQUIRED_PRODUCT_ID_MESSAGE);
        }
    }

    @Nested
    @DisplayName("Validaciones de Quantity")
    class ValidacionesQuantity {

        @Test
        @DisplayName("Debe fallar con quantity nulo")
        void debeFallarConQuantityNulo() {
            // Given
            request.setQuantity(null);
            
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations)
                    .hasSize(1)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly(REQUIRED_QUANTITY_MESSAGE);
        }

        @ParameterizedTest
        @DisplayName("Debe fallar con cantidades menores o iguales a cero")
        @ValueSource(ints = {0, -1, -5, -100})
        void debeFallarConCantidadesMenoresOIgualesACero(int cantidadInvalida) {
            // Given
            request.setQuantity(cantidadInvalida);
            
            // When
            Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);
            
            // Then
            assertThat(violations)
                    .hasSize(1)
                    .extracting(ConstraintViolation::getMessage)
                    .containsExactly(MIN_QUANTITY_MESSAGE);
        }
    }

    @Nested
    @DisplayName("Funcionalidades de Constructores")
    class FuncionalidadesConstructores {

        @Test
        @DisplayName("Debe funcionar constructor sin argumentos")
        void debeFuncionarConstructorSinArgumentos() {
            // When
            AddToCartRequest emptyRequest = new AddToCartRequest();
            
            // Then
            assertThat(emptyRequest).isNotNull();
        }

        @Test
        @DisplayName("Debe funcionar constructor con todos los argumentos")
        void debeFuncionarConstructorConTodosLosArgumentos() {
            // When
            AddToCartRequest newRequest = new AddToCartRequest(ANOTHER_PRODUCT_ID, ANOTHER_VALID_QUANTITY);
            
            // Then
            assertThat(newRequest)
                    .satisfies(r -> {
                        assertThat(r.getProductId()).isEqualTo(ANOTHER_PRODUCT_ID);
                        assertThat(r.getQuantity()).isEqualTo(ANOTHER_VALID_QUANTITY);
                    });
        }

        @Test
        @DisplayName("Debe implementar toString con información relevante")
        void debeImplementarToStringConInformacionRelevante() {
            // When
            String requestString = request.toString();
            
            // Then
            assertThat(requestString)
                    .contains("AddToCartRequest")
                    .contains(VALID_PRODUCT_ID)
                    .contains(String.valueOf(VALID_QUANTITY));
        }

        @Test
        @DisplayName("Debe implementar equals y hashCode correctamente")
        void debeImplementarEqualsYHashCodeCorrectamente() {
            // Given
            AddToCartRequest sameRequest = new AddToCartRequest(VALID_PRODUCT_ID, VALID_QUANTITY);
            AddToCartRequest differentRequest = new AddToCartRequest(ANOTHER_PRODUCT_ID, ANOTHER_VALID_QUANTITY);
            
            // Then
            assertThat(request)
                    .isEqualTo(sameRequest)
                    .isNotEqualTo(differentRequest)
                    .hasSameHashCodeAs(sameRequest);
                    
            assertThat(request.hashCode())
                    .isNotEqualTo(differentRequest.hashCode());
        }
    }

    @Test
    @DisplayName("Debe fallar con múltiples validaciones simultáneas")
    void debeFallarConMultiplesValidacionesSimultaneas() {
        // Given
        AddToCartRequest invalidRequest = AddToCartRequest.builder()
                .productId("")
                .quantity(0)
                .build();
        
        // When
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(invalidRequest);
        
        // Then
        assertThat(violations).hasSize(2);
        
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
                
        assertThat(messages).containsExactlyInAnyOrder(
                REQUIRED_PRODUCT_ID_MESSAGE,
                MIN_QUANTITY_MESSAGE
        );
    }
}