package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getComment())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment mapToComment(CommentDto dto) {
        return Comment.builder()
                .id(dto.getId())
                .comment(dto.getText())
                .build();
    }
}
