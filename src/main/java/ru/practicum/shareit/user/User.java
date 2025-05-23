package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class User {

    private Long id;
    @NotNull(message = "имя не может быть пустым")
    private String name;
    @Email
    @NotNull(message = "email не может быть пустым")
    private String email;

}
