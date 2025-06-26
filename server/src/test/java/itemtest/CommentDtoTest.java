package itemtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.comment.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testCommentDtoSerialization() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(10L)
                .text("Test comment")
                .authorName("Test")
                .created(now)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(FORMATTER));
    }

    @Test
    void testDeserializeCommentDto() throws IOException {
        String jsonString = "{\"id\":1,\"itemId\":10,\"text\":\"Test comment\",\"authorName\":\"Test\",\"created\":\"2024-10-27T10:00:00\"}";

        CommentDto commentDto = json.parse(jsonString).getObject();

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getItemId()).isEqualTo(10L);
        assertThat(commentDto.getText()).isEqualTo("Test comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("Test");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2024-10-27T10:00:00"));
    }

    @Test
    void testCommentDtoSerializationNullValues() throws IOException {
        CommentDto commentDto = CommentDto.builder().build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.itemId").isNull();
        assertThat(result).extractingJsonPathValue("$.text").isNull();
        assertThat(result).extractingJsonPathValue("$.authorName").isNull();
        assertThat(result).extractingJsonPathValue("$.created").isNull();
    }

    @Test
    void testDeserializeCommentDtoEmptyString() throws IOException {
        String jsonString = "{\"id\":1,\"itemId\":10,\"text\":\"\",\"authorName\":\"\",\"created\":\"2024-10-27T10:00:00\"}";

        CommentDto commentDto = json.parse(jsonString).getObject();

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getItemId()).isEqualTo(10L);
        assertThat(commentDto.getText()).isEqualTo("");
        assertThat(commentDto.getAuthorName()).isEqualTo("");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2024-10-27T10:00:00"));
    }

    @Test
    void testCommentDtoSerializationNullText() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(10L)
                .text(null)
                .authorName("Test")
                .created(LocalDateTime.now())
                .build();
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathValue("$.text").isNull();
    }

    @Test
    void testDeserializeCommentDtoNullCreated() throws IOException {
        String jsonString = "{\"id\":1,\"itemId\":10,\"text\":\"Test comment\",\"authorName\":\"Test\",\"created\":null}";

        CommentDto commentDto = json.parse(jsonString).getObject();

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getItemId()).isEqualTo(10L);
        assertThat(commentDto.getText()).isEqualTo("Test comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("Test");
        assertThat(commentDto.getCreated()).isNull();
    }

    @Test
    void testCommentDtoSerializationEmptyAuthorName() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(10L)
                .text("Test comment")
                .authorName("")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("");
    }

    @Test
    void testCommentDtoSerializationLongId() throws IOException {
        CommentDto commentDto = CommentDto.builder().id(Long.MAX_VALUE).itemId(1L).text("text").authorName("name").created(LocalDateTime.now()).build();
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void testCommentDtoDeserialization_invalidDate() throws IOException {
        String jsonString = "{\"id\":1,\"itemId\":10,\"text\":\"Test comment\",\"authorName\":\"Test\",\"created\":\"invalid-date\"}";
        try {
            CommentDto commentDto = json.parse(jsonString).getObject();
        } catch (Exception ignored) {

        }
    }

    @Test
    void testCommentDtoSerialization_specialCharacters() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(10L)
                .text("Comment with special characters: !@#$%^&*()")
                .authorName("Author with special characters: ~`")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Comment with special characters: !@#$%^&*()");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Author with special characters: ~`");
    }

    @Test
    void testDeserializationWithMissingCreated() throws IOException {
        String jsonString = "{\"id\":1,\"itemId\":10,\"text\":\"Test comment\",\"authorName\":\"Test\"}";
        CommentDto commentDto = json.parse(jsonString).getObject();
        assertThat(commentDto.getCreated()).isNull();
    }

    @Test
    void testSerializationAuthorNameNull() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .itemId(10L)
                .text("Test")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isNull();
    }
}