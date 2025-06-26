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
import static org.junit.jupiter.api.Assertions.*;
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
    private User existingUser;

    private User user;
    private NewUserRequest newUserRequest;
    private UpdatedUserRequest updatedUserRequest;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Создаем существующего пользователя
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Alice");
        existingUser.setEmail("alice@example.com");

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

    @Test
    void updateUser_WhenValidData_ShouldUpdateNameAndEmail() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setName("Bob");
        request.setEmail("bob@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());

        verify(userRepository).save(argThat(u -> u.getName().equals("Bob") && u.getEmail().equals("bob@example.com")));
    }

    @Test
    void updateUser_WhenOnlyNameProvided_ShouldUpdateNameOnly() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());
        verify(userRepository).save(argThat(u -> u.getName().equals("New Name")));
    }

    @Test
    void updateUser_WhenOnlyEmailProvided_ShouldUpdateEmail() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setEmail("new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("new@email.com", result.getEmail());
        verify(userRepository).save(argThat(u -> u.getEmail().equals("new@email.com")));
    }

    @Test
    void updateUser_WhenEmailIsAlreadyTaken_ShouldThrowDuplicatedException() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // When + Then
        DuplicatedException exception = assertThrows(DuplicatedException.class,
                () -> userService.updateUser(1L, request));

        assertTrue(exception.getMessage().contains("уже зарегистрирован"));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setName("NotExistingUser");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When + Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, request));

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void updateUser_WhenNoUpdatesProvided_ShouldReturnSameUser() {
        // Given
        UpdatedUserRequest emptyRequest = new UpdatedUserRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userMapper.mapToDto(any())).thenReturn(UserDto.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .build());
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // When
        UserDto result = userService.updateUser(1L, emptyRequest);

        // Then
        assertNotNull(result);
        assertEquals(existingUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_WhenEmailIsTheSame_ShouldNotCheckDuplicates() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setEmail("alice@example.com"); // тот же email

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.mapToDto(existingUser)).thenReturn(UserDto.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .build());



        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("alice@example.com", result.getEmail());
        verify(userRepository, never()).existsByEmail(anyString()); // не проверяем на дубликат
    }


    @Test
    void updateUser_WhenEmptyEmailAndName_ShouldNotChangeAnything() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setEmail(""); // пустая строка
        request.setName("");  // пустая строка

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userMapper.mapToDto(any())).thenReturn(UserDto.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .build());
        when(userRepository.existsByEmail(any())).thenReturn(false);


        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("", request.getName());
        assertEquals("", request.getEmail());
    }

    @Test
    void updateUser_WhenBlankName_ShouldPreserveIt() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setName("   "); // пробелы

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        when(userRepository.existsByEmail(any())).thenReturn(false);

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(existingUser.getName(), result.getName());
    }

    @Test
    void updateUser_WhenNullName_ShouldPreserveOldValue() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUser_WhenNullEmail_ShouldPreserveOldValue() {
        // Given
        UpdatedUserRequest request = new UpdatedUserRequest();
        request.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.mapToDto(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return UserDto.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .build();
        });

        // When
        UserDto result = userService.updateUser(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

}