package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getComment())
                .itemId((comment.getItem() != null) ? comment.getItem().getId() : null)
                .authorName((comment.getAuthor() != null) ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .build();
    }

    public static Comment mapToComment(CommentDto dto) {
        return Comment.builder()
                .id(dto.getId())
                .comment(dto.getText())
                .created(dto.getCreated())
                .build();
    }
}
