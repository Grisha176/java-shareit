package itemtest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class CommentMapperTest {


    private CommentMapper mapper = new CommentMapper();

    @Test
    void mapToDto_ReturnsCorrectDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Item item = Item.builder().id(1L).name("Test item").description("Desc").available(true).build();
        User user = User.builder().id(100L).name("User").email("user@example.com").build();

        Comment comment = Comment.builder()
                .id(1L)
                .comment("Great item!")
                .item(item)
                .author(user)
                .created(now)
                .build();

        // When
        CommentDto dto = mapper.mapToDto(comment);

        // Then
        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getComment(), dto.getText());
        assertEquals(item.getId(), dto.getItemId());
        assertEquals(user.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void mapToComment_ReturnsCorrectEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Nice tool")
                .itemId(10L)
                .authorName("Alice")
                .created(now)
                .build();

        // When
        Comment comment = CommentMapper.mapToComment(dto);

        // Then
        assertNotNull(comment);
        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getComment());
        assertNull(comment.getItem());  // Item не устанавливается в mapToComment
        assertNull(comment.getAuthor()); // Author тоже не устанавливается
        assertEquals(dto.getCreated(), comment.getCreated());
    }

    @Test
    void mapToDto_WithNullFields_ShouldHandleGracefully() {
        // Given
        Comment comment = Comment.builder()
                .id(1L)
                .comment("No item or author")
                .author(null)
                .item(null)
                .build(); // item и author == null

        // When
        CommentDto dto = mapper.mapToDto(comment);

        // Then
        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getComment(), dto.getText());
        assertNull(dto.getItemId());
        assertNull(dto.getAuthorName());
        assertNull(dto.getCreated());
    }
}