package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUser(Long userId);

    boolean hasUserWithEmail(String email);
}
