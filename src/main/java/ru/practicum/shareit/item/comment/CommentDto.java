package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;
    @NotNull(message = "комментарий не может быть пустым")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
