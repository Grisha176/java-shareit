package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapToDto(User user);

    User mapToUser(NewUserRequest request);
}
