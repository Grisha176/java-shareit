package usertest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class UserTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Given
        User user = new User();
        Long id = 1L;
        String name = "Alice";
        String email = "alice@example.com";

        // When / Then
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        User user1 = User.builder().id(1L).name("Alice").email("a@example.com").build();
        User user2 = User.builder().id(1L).name("Alice").email("a@example.com").build();
        User user3 = User.builder().id(2L).name("Bob").email("b@example.com").build();

        // Equals
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user2, user3);

        // HashCode
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }

    @Test
    void builder_ShouldCreateUserWithAllFields() {
        // When
        User user = User.builder()
                .id(100L)
                .name("Bob")
                .email("bob@test.com")
                .build();

        // Then
        assertNotNull(user);
        assertEquals(100L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@test.com", user.getEmail());
    }


    @Test
    void serializeUserToJson_ShouldIncludeAllFields() throws JsonProcessingException {
        // Given
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .build();

        // When
        String json = mapper.writeValueAsString(user);

        // Then
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"email\":\"alice@example.com\""));
    }

    @Test
    void deserializeJsonToUser_ShouldPreserveAllFields() throws JsonProcessingException {
        // Given
        String json = "{\"id\":2,\"name\":\"Bob\",\"email\":\"bob@example.com\"}";

        // When
        User user = mapper.readValue(json, User.class);

        // Then
        assertEquals(2L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
    }
}
