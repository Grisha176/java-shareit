package usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mappers.UserMapperImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

@DataJpaTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    private final UserMapper userMapper = new UserMapperImpl(); // можно использовать MapStruct

    @BeforeEach
    void setUp() {
        // Используем настоящий маппер
        this.userService = new UserServiceImpl(userRepository, userMapper);

        // Очищаем перед каждым тестом
        userRepository.deleteAll();
    }

    // --- getAllUsers ---
    @Test
    void shouldReturnAllUsers_whenNotEmpty() {
        User user1 = userRepository.save(new User(null, "John", "john@example.com"));
        User user2 = userRepository.save(new User(null, "Alice", "alice@example.com"));

        Collection<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        List<UserDto> dtoList = result.stream().toList();
        assertThat(dtoList.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(dtoList.get(1).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldReturnEmptyList_whenNoUsersExist() {
        Collection<UserDto> result = userService.getAllUsers();
        assertThat(result).isEmpty();
    }

    // --- getUserById ---
    @Test
    void shouldGetUserById_whenExists() {
        User saved = userRepository.save(new User(null, "John", "john@example.com"));

        UserDto result = userService.getUserById(saved.getId());

        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_getUserById() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    // --- createUser ---
    @Test
    void shouldCreateUser_whenEmailUnique() {
        NewUserRequest request = new NewUserRequest("John", "john@example.com");

        UserDto result = userService.createUser(request);

        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(userRepository.findByEmail("john@example.com")).isPresent();
    }

    @Test
    void shouldThrowDuplicatedException_whenEmailAlreadyExists_createUser() {
        NewUserRequest request = new NewUserRequest("John", "john@example.com");
        NewUserRequest duplicate = new NewUserRequest("Alice", "john@example.com");

        userService.createUser(request);  // first save

        assertThatThrownBy(() -> userService.createUser(duplicate))
                .isInstanceOf(DuplicatedException.class)
                .hasMessageContaining("уже зарегистрирован");
    }

    // --- updateUser ---
    @Test
    void shouldUpdateNameAndEmail_whenValidInput() {
        User saved = userRepository.save(new User(null, "John", "john@example.com"));

        UpdatedUserRequest update = new UpdatedUserRequest("New Name", "newemail@example.com");
        UserDto result = userService.updateUser(saved.getId(), update);

        assertThat(result.getName()).isEqualTo(update.getName());
        assertThat(result.getEmail()).isEqualTo(update.getEmail());
        assertThat(userRepository.findById(saved.getId()).get().getEmail()).isEqualTo(update.getEmail());
    }


    @Test
    void shouldThrowNotFoundException_whenUserNotFound_updateUser() {
        UpdatedUserRequest update = new UpdatedUserRequest("New Name", "newemail@example.com");

        assertThatThrownBy(() -> userService.updateUser(999L, update))
                .isInstanceOf(NotFoundException.class);
    }


    // --- deleteUser ---
    @Test
    void shouldDeleteUser_whenExists() {
        User saved = userRepository.save(new User(null, "John", "john@example.com"));

        userService.deleteUser(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldNotFail_whenDeletingNonExistentUser() {
        assertThatNoException()
                .isThrownBy(() -> userService.deleteUser(999L));
    }
}