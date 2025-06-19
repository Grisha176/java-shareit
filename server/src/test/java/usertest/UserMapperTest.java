package usertest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.mappers.UserMapperImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}