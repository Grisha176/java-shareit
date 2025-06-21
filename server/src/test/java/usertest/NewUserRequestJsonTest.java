package usertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.user.dto.NewUserRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class NewUserRequestJsonTest {

    @Autowired
    private JacksonTester<NewUserRequest> json;

    private NewUserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = NewUserRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();
    }

    @Test
    void serialize_ShouldWriteValidJson() throws Exception {
        JsonContent<NewUserRequest> result = json.write(validRequest);

        assertThat(result).hasJsonPath("$.name", "Alice");
        assertThat(result).hasJsonPath("$.email", "alice@example.com");
    }

    @Test
    void deserialize_ShouldReadValidJson() throws IOException {
        String content = "{\"name\":\"Bob\",\"email\":\"bob@example.com\"}";

        NewUserRequest request = json.parseObject(content);

        assertNotNull(request);
        assertEquals("Bob", request.getName());
        assertEquals("bob@example.com", request.getEmail());
    }

    @Test
    void deserialize_WithEmptyName_ShouldSetNull() throws IOException {
        String content = "{\"name\":\"\",\"email\":\"valid@example.com\"}";

        NewUserRequest request = json.parseObject(content);

        assertNotNull(request);
        assertEquals("", request.getName());
        assertEquals("valid@example.com", request.getEmail());
    }

    @Test
    void deserialize_WithNullName_ShouldSetNull() throws IOException {
        String content = "{\"name\":null,\"email\":\"valid@example.com\"}";

        NewUserRequest request = json.parseObject(content);

        assertNotNull(request);
        assertNull(request.getName());
        assertEquals("valid@example.com", request.getEmail());
    }

    @Test
    void serialize_WithNullFields_ShouldIncludeNulls() throws Exception {
        NewUserRequest request = NewUserRequest.builder().build();

        JsonContent<NewUserRequest> result = json.write(request);

        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }

    @Test
    void deserialize_WithBlankValues_ShouldPreserveBlanks() throws IOException {
        String content = "{\"name\":\"   \", \"email\":\"space@example.com\"}";

        NewUserRequest request = json.parseObject(content);

        assertNotNull(request);
        assertTrue(request.getName().isBlank());
        assertEquals("   ", request.getName());
    }
}