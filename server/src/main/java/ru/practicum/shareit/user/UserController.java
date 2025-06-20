package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@Slf4j
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userService.getAllUsers().stream().toList();
    }

    @GetMapping("/users/{id}")
    public UserDto getUsersById(@PathVariable Long id) {
        log.info("Получение пользователя с id: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public UserDto addUser(@Valid @RequestBody final NewUserRequest user) {
        log.info("Запрос на добавление пользователя");
        return userService.createUser(user);
    }

    @PatchMapping("/users/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UpdatedUserRequest user) {
        log.info("Запрос на обновление пользователя с id: {}", userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteItem(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Удаление пользователя с id: {}", userId);
    }

}
