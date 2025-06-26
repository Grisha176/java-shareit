package ru.practirum.shareit.item.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

    @NotNull(message = "комментарий не может быть пустым")
    private String text;
}
