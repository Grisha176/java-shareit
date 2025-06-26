package usertest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
    }

    @Test
    void saveUser_ShouldPersistWithGeneratedId() {
        // Given
        User savedUser = userRepository.save(user);

        // When
        User foundUser = entityManager.find(User.class, savedUser.getId());

        // Then
        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findUserById_WhenExists_ShouldReturnUser() {
        // Given
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Given
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail(user.getEmail());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void findByEmail_WhenEmailExists_ShouldReturnUser() {
        // Given
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ShouldReturnEmptyOptional() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertTrue(foundUser.isEmpty());
    }
}