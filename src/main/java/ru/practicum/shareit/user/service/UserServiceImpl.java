package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::mapToDto).toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден")));
    }

    @Override
    public UserDto createUser(NewUserRequest user) {
        User newUser = UserMapper.mapToUser(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicatedException("Пользователь: " + user + " уже зарегистрирован");
        }
        newUser = userRepository.save(newUser);
        return UserMapper.mapToDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UpdatedUserRequest updatedUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        if (userRepository.existsByEmail(updatedUser.getEmail()) && !updatedUser.getEmail().equals(user.getEmail())) {
            throw new DuplicatedException("Пользователь с email: " + updatedUser.getEmail() + " уже зарегистрирован");
        }
        user = UserMapper.updateUserFields(user, updatedUser);
        user = userRepository.save(user);
        return UserMapper.mapToDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
