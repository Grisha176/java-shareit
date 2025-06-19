package usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserController;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private NewUserRequest newUserRequest;
    private UpdatedUserRequest updatedUserRequest;

    @BeforeEach
    void setUp() {

        userDto = new UserDto(1L, "John", "john@example.com");

        newUserRequest = new NewUserRequest("John", "john@example.com");

        updatedUserRequest = new UpdatedUserRequest("John Updated", "john_updated@example.com");
    }

    @Test
    void shouldReturnAllUsers_whenGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")));

        verify(userService).getAllUsers();
    }

    @Test
    void shouldReturnUserById_whenGetUserById() throws Exception {

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldCreateUser_whenAddUser() throws Exception {

        when(userService.createUser(any(NewUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John\", \"email\":\"john@example.com\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).createUser(any(NewUserRequest.class));
    }

    @Test
    void shouldUpdateUser_whenUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdatedUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John Updated\", \"email\":\"john_updated@example.com\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).updateUser(eq(1L), any(UpdatedUserRequest.class));
    }

    @Test
    void shouldDeleteUser_whenDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{ \"Удаление прошло успешно!\" }"));

        verify(userService).deleteUser(1L);
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEmailInAddUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John\", \"email\": }"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequest_whenEmptyNameInAddUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"\", \"email\": \"john@example.com\" }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).getUserById(999L);

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }
}