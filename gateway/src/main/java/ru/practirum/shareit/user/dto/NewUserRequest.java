package ru.practirum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserRequest {

    @NotNull(message = "имя не может быть пустым")
    private String name;
    @Email(message = "Неверный тип email'la")
    @NotNull(message = "email не может быть пустым")
    private String email;
}
