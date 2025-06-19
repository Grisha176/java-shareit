package usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private final Long userId = 1L;
    private final String email = "john@example.com";
    private final String updatedEmail = "john_new@example.com";
    private final String name = "John";

    private User user;
    private NewUserRequest newUserRequest;
    private UpdatedUserRequest updatedUserRequest;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(userId, name, email);
        newUserRequest = new NewUserRequest("Alice", "alice@example.com");
        updatedUserRequest = new UpdatedUserRequest("Alice Updated", "alice_new@example.com");
        userDto = new UserDto(userId, "John", "john@example.com");
    }

    // --- getAllUsers ---
    @Test
    void shouldReturnAllUsers_whenGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapToDto(any(User.class))).thenReturn(userDto);

        Collection<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).isEqualTo(userDto);
    }

    // --- getUserById ---
    @Test
    void shouldGetUserById_whenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_getUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class);
    }

    // --- createUser ---
    @Test
    void shouldCreateUser_whenEmailUnique() {
        User newUser = new User(null, newUserRequest.getName(), newUserRequest.getEmail());
        when(userMapper.mapToUser(any())).thenReturn(newUser);
        when(userMapper.mapToDto(any())).thenReturn(userDto);
        when(userRepository.existsByEmail(newUserRequest.getEmail())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(user);

        UserDto result = userService.createUser(newUserRequest);

        verify(userRepository, times(1)).save(newUser);
        assertThat(result.getName()).isEqualTo(userDto.getName());
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void shouldThrowDuplicatedException_whenEmailAlreadyExists_createUser() {
        when(userMapper.mapToUser(newUserRequest)).thenReturn(new User(null, newUserRequest.getName(), newUserRequest.getEmail()));
        when(userRepository.existsByEmail(newUserRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(newUserRequest))
                .isInstanceOf(DuplicatedException.class);
    }

    // --- updateUser ---

    @Test
    void shouldNotChangeEmail_whenEmailSameAsOld() {
        UpdatedUserRequest sameEmailRequest = new UpdatedUserRequest("New Name", email);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, sameEmailRequest);

        assertThat(result.getEmail()).isEqualTo(email); // не менялось
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_updateUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, updatedUserRequest))
                .isInstanceOf(NotFoundException.class);
    }



    // --- deleteUser ---
    @Test
    void shouldDeleteUser_whenExists() {
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    // --- mapToDto / mapToUser helpers ---
    @Test
    void shouldMapUserToUserDto() {
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userMapper.mapToDto(user);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

}