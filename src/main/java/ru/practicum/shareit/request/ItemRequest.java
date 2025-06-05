package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {

    private Long id;
    @NotNull(message = "описание не может быть пустым")
    private String description;
    private Long requestorId;
    private LocalDateTime createdTime;

}
