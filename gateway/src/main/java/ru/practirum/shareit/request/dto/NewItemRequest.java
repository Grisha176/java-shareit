package ru.practirum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewItemRequest {

    @NotNull(message = "описание не может быть пустым")
    private String description;
}
