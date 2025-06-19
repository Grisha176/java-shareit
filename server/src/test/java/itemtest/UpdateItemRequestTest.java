package itemtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class UpdateItemRequestTest {

    @Autowired
    private JacksonTester<UpdateItemRequest> json;

    private UpdateItemRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateItemRequest();
    }

    // --- hasName() ---

    @Test
    void hasName_WhenNameIsNotNullAndNotBlank_ShouldReturnTrue() {
        request.setName("Drill");
        assertTrue(request.hasName());
    }

    @Test
    void hasName_WhenNameIsNull_ShouldReturnFalse() {
        request.setName(null);
        assertFalse(request.hasName());
    }

    @Test
    void hasName_WhenNameIsEmpty_ShouldReturnFalse() {
        request.setName("");
        assertFalse(request.hasName());
    }

    @Test
    void hasName_WhenNameIsBlank_ShouldReturnFalse() {
        request.setName("   ");
        assertFalse(request.hasName());
    }

    // --- hasDescription() ---

    @Test
    void hasDescription_WhenDescriptionIsNotNullAndNotBlank_ShouldReturnTrue() {
        request.setDescription("Powerful drill");
        assertTrue(request.hasDescription());
    }

    @Test
    void hasDescription_WhenDescriptionIsNull_ShouldReturnFalse() {
        request.setDescription(null);
        assertFalse(request.hasDescription());
    }

    @Test
    void hasDescription_WhenDescriptionIsEmpty_ShouldReturnFalse() {
        request.setDescription("");
        assertFalse(request.hasDescription());
    }

    @Test
    void hasDescription_WhenDescriptionIsBlank_ShouldReturnFalse() {
        request.setDescription("   ");
        assertFalse(request.hasDescription());
    }

    // --- hasAvailable() ---

    @Test
    void hasAvailable_WhenAvailableIsTrue_ShouldReturnTrue() {
        request.setAvailable(true);
        assertTrue(request.hasAvailable());
    }

    @Test
    void hasAvailable_WhenAvailableIsFalse_ShouldReturnTrue() {
        request.setAvailable(false);
        assertTrue(request.hasAvailable());
    }

    @Test
    void hasAvailable_WhenAvailableIsNull_ShouldReturnFalse() {
        request.setAvailable(null);
        assertFalse(request.hasAvailable());
    }

    // --- JSON сериализация / десериализация ---

    @Test
    void serialize_WithAllFields_ShouldIncludeAllInJson() throws Exception {
        request.setName("Drill");
        request.setDescription("Powerful tool");
        request.setAvailable(true);
        request.setOwnerId(100L);

        var result = json.write(request);

        assertThat(result).hasJsonPath("$.name", "Drill");
        assertThat(result).hasJsonPath("$.description", "Powerful tool");
        assertThat(result).hasJsonPath("$.available", true);
        assertThat(result).hasJsonPath("$.ownerId", 100L);
    }

    @Test
    void deserialize_WithFullJson_ShouldSetAllFields() throws IOException {
        String content = """
                {  "name": "Saw",  "description": "Hand saw", "available": false,   "ownerId": 200 }
                """;

        UpdateItemRequest dto = json.parseObject(content);

        assertNotNull(dto);
        assertEquals("Saw", dto.getName());
        assertEquals("Hand saw", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertEquals(200L, dto.getOwnerId().longValue());
    }

    @Test
    void serialize_WithNullValues_ShouldIncludeNulls() throws Exception {
        request.setName(null);
        request.setDescription(null);
        request.setAvailable(null);
        request.setOwnerId(null);

        var result = json.write(request);

        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathValue("$.available").isNull();
        assertThat(result).extractingJsonPathValue("$.ownerId").isNull();
    }

    @Test
    void deserialize_WithMissingFields_ShouldSetToNull() throws IOException {
        String content = "{}";

        UpdateItemRequest dto = json.parseObject(content);

        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getAvailable());
        assertNull(dto.getOwnerId());
    }

    @Test
    void deserialize_WithEmptyName_ShouldPreserveIt() throws IOException {
        String content = "{\"name\":\"\",\"description\":\"Some description\"}";

        UpdateItemRequest dto = json.parseObject(content);

        assertNotNull(dto);
        assertEquals("", dto.getName());
        assertEquals("Some description", dto.getDescription());
    }

    @Test
    void deserialize_WithBlankName_ShouldPreserveIt() throws IOException {
        String content = "{\"name\":\"   \",\"description\":\"Some description\"}";

        UpdateItemRequest dto = json.parseObject(content);

        assertNotNull(dto);
        assertEquals("   ", dto.getName());
        assertEquals("Some description", dto.getDescription());
    }

    @Test
    void serialize_WithNameOnly_ShouldNotFail() throws Exception {
        request.setName("Hammer");

        var result = json.write(request);

        assertThat(result).hasJsonPath("$.name", "Hammer");
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathValue("$.available").isNull();
        assertThat(result).extractingJsonPathValue("$.ownerId").isNull();
    }

    @Test
    void serialize_WithOwnerOnly_ShouldIncludeOwnerId() throws Exception {
        request.setOwnerId(300L);

        var result = json.write(request);

        assertThat(result).hasJsonPath("$.ownerId", 300L);
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathValue("$.available").isNull();
    }

}