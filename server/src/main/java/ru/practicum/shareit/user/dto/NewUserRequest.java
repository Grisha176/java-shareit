package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewUserRequest {

    @NotNull(message = "имя не может быть пустым")
    @NotBlank
    private String name;
    @Email
    @NotNull(message = "email не может быть пустым")
    private String email;
}
