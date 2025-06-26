package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    @NotNull(message = "комментарий не может быть пустым")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
