package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Item {

    private Long id;
    @NotNull(message = "имя не может быть пустым")
    @NotBlank
    private String name;
    @NotNull(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
    private String request;


}
