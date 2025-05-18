package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private Long id;
    @NotNull(message = "описание не может быть пустым")
    private String description;
    private Long requestorId;
    private LocalDateTime createdTime;

}
