package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User createUser(User user) {
        user.setId(generatedId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private Long generatedId() {
        return (long) (users.size() + 1);
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        List<String> emails = users.values().stream().map(user -> user.getEmail()).toList();
        return emails.contains(email);
    }


}
