package ru.practirum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practirum.shareit.user.dto.NewUserRequest;
import ru.practirum.shareit.user.dto.UpdateUserRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получение всех пользователей");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUsersById(@PathVariable Long id) {
        log.info("Получение пользователя с id: {}", id);
        return userClient.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody final NewUserRequest user) {
        log.info("Запрос на добавление пользователя");
        return userClient.create(user);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UpdateUserRequest user) {
        log.info("Запрос на обновление пользователя с id: {}", userId);
        return userClient.update(userId,user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        return userClient.deleteById(userId);
    }

}
