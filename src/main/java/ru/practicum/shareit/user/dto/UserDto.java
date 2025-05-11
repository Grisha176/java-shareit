package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    @NotNull(message = "id не может быть пустым")
    private Long id;
    @NotNull(message = "имя не может быть пустым")
    private String name;
    @Email
    @NotNull(message = "email не может быть пустым")
    private String email;
}
