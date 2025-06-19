package ru.practirum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {

    @NotNull(message = "имя не может быть пустым")
    @NotBlank
    private String name;
    @NotNull(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;

}
