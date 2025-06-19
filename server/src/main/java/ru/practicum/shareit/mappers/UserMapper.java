package ru.practicum.shareit.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    UserDto mapToDto(User user);

    @BeanMapping(ignoreByDefault = true)
    User mapToUser(NewUserRequest request);
}
