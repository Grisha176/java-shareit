package usertest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.mappers.UserMapperImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class UserMapperTest {


    private UserMapperImpl userMapper = new UserMapperImpl();

    @Test
    void toUserFullReturnsMappedObjectTest() {

        NewUserRequest userDto = NewUserRequest.builder().name("Test").email("test@yandex.ru").build();

        User mappedUser = userMapper.mapToUser(userDto);

        assertEquals(mappedUser.getName(), userDto.getName());
        assertEquals(mappedUser.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserReturnsMappedObjectWithNullFieldsTest() {

        NewUserRequest userDto = NewUserRequest.builder().name("").email(null).build();


        User mappedUser = userMapper.mapToUser(userDto);

        assertNull(mappedUser.getId());
        assertEquals("", mappedUser.getName());
        assertNull(mappedUser.getEmail());
    }

    @Test
    void toUserReturnsMappedObjectWithPartialDataTest() {
        NewUserRequest userDto = NewUserRequest.builder().name("Test").email(null).build();


        User mappedUser = userMapper.mapToUser(userDto);

        assertEquals(mappedUser.getName(), userDto.getName());
        assertNull(mappedUser.getEmail());
    }

    @Test
    void mapToDto_WhenUserIsNotNull_ShouldMapAllFields() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        // When
        UserDto dto = userMapper.mapToDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void mapToDto_WhenUserIsNull_ShouldReturnNull() {
        // When
        UserDto dto = userMapper.mapToDto(null);

        // Then
        assertNull(dto);
    }

    @Test
    void mapToUser_WhenRequestIsNotNull_ShouldCreateUserWithCorrectFields() {
        // Given
        NewUserRequest request = NewUserRequest.builder()
                .name("Bob")
                .email("bob@example.com")
                .build();

        // When
        User user = userMapper.mapToUser(request);

        // Then
        assertNotNull(user);
        assertNull(user.getId()); // не устанавливается через NewUserRequest
        assertEquals(request.getName(), user.getName());
        assertEquals(request.getEmail(), user.getEmail());
    }

    @Test
    void mapToUser_WhenRequestIsNull_ShouldReturnNull() {
        // When
        User user = userMapper.mapToUser(null);

        // Then
        assertNull(user);
    }

    @Test
    void mapToDto_WhenUserHasNullFields_ShouldPreserveThem() {
        // Given
        User user = new User();
        user.setId(2L);
        user.setName(null);  // name == null
        user.setEmail("");   // email == empty

        // When
        UserDto dto = userMapper.mapToDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertNull(dto.getName());
        assertEquals("", dto.getEmail());
    }

    @Test
    void mapToUser_WhenRequestHasBlankEmail_ShouldSetEmptyEmail() {
        // Given
        NewUserRequest request = new NewUserRequest();
        request.setName("Charlie");
        request.setEmail("   ");

        // When
        User user = userMapper.mapToUser(request);

        // Then
        assertNotNull(user);
        assertEquals("Charlie", user.getName());
        assertEquals("   ", user.getEmail());
    }

    @Test
    void mapToDto_WhenUserHasBlankName_ShouldPreserveIt() {
        // Given
        User user = new User();
        user.setId(3L);
        user.setName("   ");
        user.setEmail("blank@example.com");

        // When
        UserDto dto = userMapper.mapToDto(user);

        // Then
        assertNotNull(dto);
        assertEquals("   ", dto.getName());
        assertEquals("blank@example.com", dto.getEmail());
    }

    @Test
    void mapToUser_WhenRequestHasEmptyFields_ShouldSetEmptyValues() {
        // Given
        NewUserRequest request = new NewUserRequest();
        request.setName("");
        request.setEmail("");

        // When
        User user = userMapper.mapToUser(request);

        // Then
        assertNotNull(user);
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
    }

    @Test
    void mapToDto_WhenUserHasWhitespace_ShouldNotFail() {
        // Given
        User user = new User();
        user.setId(4L);
        user.setName("   Alice   ");
        user.setEmail(" alice@example.com ");

        // When
        UserDto dto = userMapper.mapToDto(user);

        // Then
        assertNotNull(dto);
        assertEquals("   Alice   ", dto.getName());
        assertEquals(" alice@example.com ", dto.getEmail());
    }

    @Test
    void mapToUser_WhenRequestHasValidData_ShouldCreateUser() {
        // Given
        NewUserRequest request = NewUserRequest.builder()
                .name("David")
                .email("david@example.com")
                .build();

        // When
        User user = userMapper.mapToUser(request);

        // Then
        assertNotNull(user);
        assertEquals("David", user.getName());
        assertEquals("david@example.com", user.getEmail());
    }
}