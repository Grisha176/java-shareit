package usertest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.user.dto.UpdatedUserRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class UpdatedUserRequestTest {

    @Autowired
    private JacksonTester<UpdatedUserRequest> jacksonTester;

    private final UpdatedUserRequest request = new UpdatedUserRequest();

    @Test
    void testHasEmail_ReturnsTrue_WhenEmailIsNotBlank() {
        request.setEmail("valid@example.com");
        assertTrue(request.hasEmail());
    }

    @Test
    void testHasEmail_ReturnsFalse_WhenEmailIsNull() {
        request.setEmail(null);
        assertFalse(request.hasEmail());
    }

    @Test
    void testHasEmail_ReturnsFalse_WhenEmailIsEmpty() {
        request.setEmail("");
        assertFalse(request.hasEmail());
    }

    @Test
    void testHasEmail_ReturnsFalse_WhenEmailIsBlank() {
        request.setEmail("   ");
        assertFalse(request.hasEmail());
    }

    @Test
    void testHasUsername_ReturnsTrue_WhenNameIsNotBlank() {
        request.setName("Valid Name");
        assertTrue(request.hasUsername());
    }

    @Test
    void testHasUsername_ReturnsFalse_WhenNameIsNull() {
        request.setName(null);
        assertFalse(request.hasUsername());
    }

    @Test
    void testHasUsername_ReturnsFalse_WhenNameIsEmpty() {
        request.setName("");
        assertFalse(request.hasUsername());
    }

    @Test
    void testHasUsername_ReturnsFalse_WhenNameIsBlank() {
        request.setName("   ");
        assertFalse(request.hasUsername());
    }

    @Test
    void testSerialize_WithValidFields() throws Exception {
        request.setName("Alice");
        request.setEmail("alice@example.com");

        var result = jacksonTester.write(request);

        assertThat(result).hasJsonPath("$.name", "Alice");
        assertThat(result).hasJsonPath("$.email", "alice@example.com");
    }

    @Test
    void testDeserialize_WithValidJson() throws IOException {
        String json = "{\"name\":\"Bob\",\"email\":\"bob@example.com\"}";

        UpdatedUserRequest deserialized = jacksonTester.parseObject(json);

        assertTrue(deserialized.hasUsername());
        assertTrue(deserialized.hasEmail());
        assertEquals("Bob", deserialized.getName());
        assertEquals("bob@example.com", deserialized.getEmail());
    }

    @Test
    void testSerialize_WithNameOnly() throws Exception {
        request.setName("Charlie");

        var result = jacksonTester.write(request);

        assertThat(result).hasJsonPath("$.name", "Charlie");
    }

    @Test
    void testDeserialize_WithNameOnly_ShouldSetEmailToNull() throws IOException {
        String json = "{\"name\":\"Diana\"}";

        UpdatedUserRequest deserialized = jacksonTester.parseObject(json);

        assertTrue(deserialized.hasUsername());
        assertFalse(deserialized.hasEmail());
        assertEquals("Diana", deserialized.getName());
        assertNull(deserialized.getEmail());
    }

    @Test
    void testSerialize_WithEmailOnly() throws Exception {
        request.setEmail("diana@example.com");

        var result = jacksonTester.write(request);

        assertThat(result).hasJsonPath("$.email", "diana@example.com");
    }

    @Test
    void testDeserialize_WithEmailOnly_ShouldSetNameToNull() throws IOException {
        String json = "{\"email\":\"eve@example.com\"}";

        UpdatedUserRequest deserialized = jacksonTester.parseObject(json);

        assertTrue(deserialized.hasEmail());
        assertFalse(deserialized.hasUsername());
        assertEquals("eve@example.com", deserialized.getEmail());
        assertNull(deserialized.getName());
    }

    @Test
    void testSerialize_NullValues() throws Exception {
        UpdatedUserRequest userRequest = new UpdatedUserRequest();
        var result = jacksonTester.write(userRequest);

        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void testDeserialize_EmptyJson() throws IOException {
        String json = "{}";

        UpdatedUserRequest deserialized = jacksonTester.parseObject(json);

        assertFalse(deserialized.hasUsername());
        assertFalse(deserialized.hasEmail());
        assertNull(deserialized.getName());
        assertNull(deserialized.getEmail());
    }

    @Test
    void testDeserialize_BlankValues() throws IOException {
        String json = "{\"name\": \"   \", \"email\": \"   \"}";

        UpdatedUserRequest deserialized = jacksonTester.parseObject(json);

        assertFalse(deserialized.hasUsername());
        assertFalse(deserialized.hasEmail());
        assertEquals("   ", deserialized.getName());
        assertEquals("   ", deserialized.getEmail());
    }
}
