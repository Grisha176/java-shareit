package itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.RespondItemRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class RespondItemRequestTest {

    @Autowired
    private JacksonTester<RespondItemRequest> json;

    private RespondItemRequest request;

    @BeforeEach
    void setUp() {
        request = new RespondItemRequest();
        request.setId(1L);
        request.setName("Drill");
        request.setOwnerId(100L);
    }

    @Test
    void testSerialize_WithValidFields_ShouldWriteAllValues() throws Exception {
        // When
        var result = json.write(request);

        // Then
        assertThat(result).hasJsonPath("$.id", 1L);
        assertThat(result).hasJsonPath("$.name", "Drill");
        assertThat(result).hasJsonPath("$.ownerId", 100L);
    }

    @Test
    void testDeserialize_WithFullJson_ShouldSetAllFields() throws IOException {
        // Given
        String content = "{\"id\":2,\"name\":\"Hammer\",\"ownerId\":200}";

        // When
        RespondItemRequest deserialized = json.parseObject(content);

        // Then
        assertNotNull(deserialized);
        assertEquals(2L, deserialized.getId());
        assertEquals("Hammer", deserialized.getName());
        assertEquals(200L, deserialized.getOwnerId());
    }

    @Test
    void testSerialize_WithNullId_ShouldIncludeNull() throws Exception {
        // Given
        request.setId(null);

        // When
        var result = json.write(request);

        // Then
        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).hasJsonPath("$.name", "Drill");
        assertThat(result).hasJsonPath("$.ownerId", 100L);
    }

    @Test
    void testDeserialize_WithMissingId_ShouldSetIdToNull() throws IOException {
        // Given
        String content = "{\"name\":\"Saw\",\"ownerId\":300}";

        // When
        RespondItemRequest deserialized = json.parseObject(content);

        // Then
        assertNotNull(deserialized);
        assertNull(deserialized.getId());
        assertEquals("Saw", deserialized.getName());
        assertEquals(300L, deserialized.getOwnerId());
    }

    @Test
    void testSerialize_WithNameOnly_ShouldNotFail() throws Exception {
        // Given
        request.setId(null);
        request.setOwnerId(null);

        // When
        var result = json.write(request);

        // Then
        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).hasJsonPath("$.name", "Drill");
        assertThat(result).extractingJsonPathValue("$.ownerId").isNull();
    }

    @Test
    void testDeserialize_WithEmptyJson_ShouldReturnNullFields() throws IOException {
        // Given
        String content = "{}";

        // When
        RespondItemRequest deserialized = json.parseObject(content);

        // Then
        assertNotNull(deserialized);
        assertNull(deserialized.getId());
        assertNull(deserialized.getName());
        assertNull(deserialized.getOwnerId());
    }

    @Test
    void testDeserialize_WithBlankName_ShouldPreserveBlank() throws IOException {
        // Given
        String content = "{\"id\":5,\"name\":\"   \",\"ownerId\":400}";

        // When
        RespondItemRequest deserialized = json.parseObject(content);

        // Then
        assertNotNull(deserialized);
        assertEquals(5L, deserialized.getId());
        assertEquals("   ", deserialized.getName());  // blank name
        assertEquals(400L, deserialized.getOwnerId());
    }

    @Test
    void testEqualsAndHashCode_ShouldWorkCorrectly() {
        RespondItemRequest r1 = new RespondItemRequest();
        r1.setId(1L);
        r1.setName("Drill");
        r1.setOwnerId(100L);

        RespondItemRequest r2 = new RespondItemRequest();
        r2.setId(1L);
        r2.setName("Drill");
        r2.setOwnerId(100L);

        RespondItemRequest r3 = new RespondItemRequest();
        r3.setId(2L);
        r3.setName("Drill");
        r3.setOwnerId(100L);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }
}