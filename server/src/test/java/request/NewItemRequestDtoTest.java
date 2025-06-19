package request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class NewItemRequestDtoTest {

    @Autowired
    private JacksonTester<NewItemRequestDto> json;

    private NewItemRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = new NewItemRequestDto();
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        String description = "Need a drill";

        dto.setDescription(description);

        assertNotNull(dto);
        assertEquals(description, dto.getDescription());
    }

    @Test
    void whenDescriptionIsNull_ShouldHandleGracefully() {
        dto.setDescription(null);

        assertNull(dto.getDescription());
    }

    @Test
    void whenDescriptionIsEmpty_ShouldPreserveEmptyValue() {
        dto.setDescription("");

        assertEquals("", dto.getDescription());
    }

    @Test
    void whenDescriptionIsBlank_ShouldPreserveBlank() {
        dto.setDescription("   ");

        assertEquals("   ", dto.getDescription());
    }

    @Test
    void serializeToJson_ShouldIncludeDescription() throws IOException {
        dto.setDescription("Looking for a book");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.description", "Looking for a book");
    }

    @Test
    void deserializeFromJson_ShouldPreserveDescription() throws IOException {
        String content = "{\"description\": \"Powerful drill\"}";

        NewItemRequestDto parsed = json.parseObject(content);

        assertNotNull(parsed);
        assertEquals("Powerful drill", parsed.getDescription());
    }

    @Test
    void deserializeWithNullDescription_ShouldReturnNull() throws IOException {
        String content = "{\"description\": null}";

        NewItemRequestDto parsed = json.parseObject(content);

        assertNull(parsed.getDescription());
    }

    @Test
    void toString_ShouldIncludeDescription() {
        dto.setDescription("Some request");

        String result = dto.toString();

        assertNotNull(result);
    }
}