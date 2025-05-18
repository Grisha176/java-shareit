package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewUserRequest {

    @NotNull(message = "имя не может быть пустым")
    private String name;
    @Email
    @NotNull(message = "email не может быть пустым")
    private String email;
}
