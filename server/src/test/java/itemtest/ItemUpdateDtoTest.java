package itemtest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemUpdateDtoTest {

    @Autowired
    private ObjectMapper json;

    @Test
    void testItemUpdateDtoSerialization() throws IOException {
        UpdateItemRequest itemUpdateDto = UpdateItemRequest.builder()
                .name("New Item Name")
                .description("New Item Description")
                .available(true)
                .build();

        String jsonString = json.writeValueAsString(itemUpdateDto);

        assertThat(jsonString).contains("\"name\":\"New Item Name\"");
        assertThat(jsonString).contains("\"description\":\"New Item Description\"");
        assertThat(jsonString).contains("\"available\":true");
    }

    @Test
    void testItemUpdateDtoDeserialization() throws IOException {
        String jsonString = "{\"name\":\"Updated Name\", \"description\":\"Updated Description\", \"available\":false}";

        UpdateItemRequest itemUpdateDto = json.readValue(jsonString, UpdateItemRequest.class);

        assertThat(itemUpdateDto.getName()).isEqualTo("Updated Name");
        assertThat(itemUpdateDto.getDescription()).isEqualTo("Updated Description");
        assertThat(itemUpdateDto.getAvailable()).isFalse();
    }

    @Test
    void testItemUpdateDtoSerializationWithNullValues() throws IOException {
        UpdateItemRequest itemUpdateDto = UpdateItemRequest.builder()
                .name(null)
                .description("Description")
                .available(null)
                .build();

        String jsonString = json.writeValueAsString(itemUpdateDto);
        assertThat(jsonString).contains("\"description\":\"Description\"");
    }

    @Test
    void testItemUpdateDtoDeserializationWithEmptyValues() throws IOException {
        String jsonString = "{\"name\":\"\", \"description\":\"\", \"available\":null, \"requestId\":null}";
        UpdateItemRequest itemUpdateDto = json.readValue(jsonString, UpdateItemRequest.class);

        assertThat(itemUpdateDto.getName()).isEqualTo("");
        assertThat(itemUpdateDto.getDescription()).isEqualTo("");
        assertThat(itemUpdateDto.getAvailable()).isNull();
    }

    @Test
    void testItemUpdateDtoDeserialization_emptyJson() throws IOException {
        String jsonString = "{}";

        UpdateItemRequest itemUpdateDto = json.readValue(jsonString, UpdateItemRequest.class);

        assertThat(itemUpdateDto.getName()).isNull();
        assertThat(itemUpdateDto.getDescription()).isNull();
        assertThat(itemUpdateDto.getAvailable()).isNull();
    }

    @Test
    void testItemUpdateDtoSerialization_emptyStringValues() throws IOException {
        UpdateItemRequest itemUpdateDto = UpdateItemRequest.builder()
                .name("")
                .description("")
                .build();

        String jsonString = json.writeValueAsString(itemUpdateDto);

        assertThat(jsonString).contains("\"name\":\"\"");
        assertThat(jsonString).contains("\"description\":\"\"");
    }

    @Test
    void testItemUpdateDtoDeserialization_booleanValues() throws IOException {
        String jsonString = "{\"available\":true}";
        UpdateItemRequest itemUpdateDto = json.readValue(jsonString, UpdateItemRequest.class);

        assertThat(itemUpdateDto.getAvailable()).isTrue();

        jsonString = "{\"available\":false}";
        itemUpdateDto = json.readValue(jsonString, UpdateItemRequest.class);

        assertThat(itemUpdateDto.getAvailable()).isFalse();
    }
}
