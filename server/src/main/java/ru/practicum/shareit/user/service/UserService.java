package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(NewUserRequest user);

    UserDto updateUser(Long userId, UpdatedUserRequest updatedUser);

    void deleteUser(Long userId);
}