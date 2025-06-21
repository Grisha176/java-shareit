package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;


    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(mapper::mapToDto).toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return mapper.mapToDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден")));
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest user) {
        User newUser = mapper.mapToUser(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicatedException("Пользователь: " + user + " уже зарегистрирован");
        }
        newUser = userRepository.save(newUser);
        log.info("Добавление пользователя с id: {}", newUser.getId());
        return mapper.mapToDto(newUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UpdatedUserRequest updatedUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail()) && !updatedUser.getEmail().equals(user.getEmail())) {
                throw new DuplicatedException("Пользователь с email: " + updatedUser.getEmail() + " уже зарегистрирован");
            }
        }

        user = updateUserFields(user, updatedUser);
        user = userRepository.save(user);
        log.info("Обновелние пользователя с id:{}", userId);
        return mapper.mapToDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private static User updateUserFields(User user, UpdatedUserRequest updateUserRequest) {
        if (updateUserRequest.hasEmail()) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.hasUsername()) {
            user.setName(updateUserRequest.getName());
        }
        return user;
    }
}
