package usertest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NewUserRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidRequest_thenNoViolations() {
        NewUserRequest request = NewUserRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsNull_thenHasConstraintViolation() {
        NewUserRequest request = NewUserRequest.builder()
                .email("test@example.com")
                .build();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenEmailIsNull_thenHasConstraintViolation() {
        NewUserRequest request = NewUserRequest.builder()
                .name("Alice")
                .build();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenEmailIsInvalid_thenHasConstraintViolation() {
        NewUserRequest request = NewUserRequest.builder()
                .name("Alice")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameIsBlank_thenHasConstraintViolation() {
        NewUserRequest request = NewUserRequest.builder()
                .name("   ")
                .email("valid@email.com")
                .build();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}