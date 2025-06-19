package itemtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemTest {

    @Autowired
    private JacksonTester<Item> jacksonTester;

    private Item item;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Alice");
        owner.setEmail("alice@example.com");

        item = Item.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .requestId(200L)
                .build();
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        assertEquals(100L, item.getId().longValue());
        assertEquals("Drill", item.getName());
        assertEquals("Powerful drill", item.getDescription());
        assertTrue(item.isAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(200L, item.getRequestId().longValue());
    }

    @Test
    void setters_ShouldUpdateFields() {
        // Given
        User newOwner = new User();
        newOwner.setId(2L);
        newOwner.setName("Bob");
        newOwner.setEmail("bob@example.com");

        // When
        item.setId(101L);
        item.setName("Saw");
        item.setDescription("Hand saw");
        item.setAvailable(false);
        item.setOwner(newOwner);
        item.setRequestId(201L);

        // Then
        assertEquals(101L, item.getId().longValue());
        assertEquals("Saw", item.getName());
        assertEquals("Hand saw", item.getDescription());
        assertFalse(item.isAvailable());
        assertEquals(newOwner, item.getOwner());
        assertEquals(201L, item.getRequestId().longValue());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        Item item1 = Item.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .requestId(200L)
                .build();

        Item item2 = Item.builder()
                .id(100L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .requestId(200L)
                .build();

        Item item3 = Item.builder()
                .id(101L)
                .name("Different Drill")
                .description("Another drill")
                .available(false)
                .owner(owner)
                .requestId(200L)
                .build();

        // Equals
        assertEquals(item1, item2);
        assertNotEquals(item1, item3);

        // HashCode
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
        assertThat(item1.hashCode()).isNotEqualTo(item3.hashCode());
    }


    @Test
    void serializeToJson_ShouldIncludeAllFields() throws IOException {
        var jsonContent = jacksonTester.write(item);

        assertThat(jsonContent).hasJsonPath("$.id", 100L);
        assertThat(jsonContent).hasJsonPath("$.name", "Drill");
        assertThat(jsonContent).hasJsonPath("$.description", "Powerful drill");
        assertThat(jsonContent).hasJsonPath("$.available", true);
        assertThat(jsonContent).hasJsonPath("$.owner.id", 1L);
        assertThat(jsonContent).hasJsonPath("$.requestId", 200L);
    }

    @Test
    void deserializeFromJson_ShouldPreserveAllFields() throws IOException {
        String content = "{ \"id\": 101, \"name\": \"Saw\", \"description\": \"Hand saw\", \"available\": false, \"owner\": { \"id\": 2, \"name\": \"Bob\", \"email\": \"bob@example.com\" }, \"requestId\": 201 }";

        Item parsedItem = jacksonTester.parseObject(content);

        assertNotNull(parsedItem);
        assertEquals(101L, parsedItem.getId().longValue());
        assertEquals("Saw", parsedItem.getName());
        assertEquals("Hand saw", parsedItem.getDescription());
        assertFalse(parsedItem.isAvailable());

        assertNotNull(parsedItem.getOwner());
        assertEquals(2L, parsedItem.getOwner().getId().longValue());
        assertEquals("Bob", parsedItem.getOwner().getName());
        assertEquals("bob@example.com", parsedItem.getOwner().getEmail());

        assertEquals(201L, parsedItem.getRequestId().longValue());
    }

    @Test
    void builder_ShouldCreateItemWithAllFields() {
        Item built = Item.builder()
                .id(200L)
                .name("Table")
                .description("Wooden table")
                .available(false)
                .owner(owner)
                .requestId(300L)
                .build();

        assertNotNull(built);
        assertEquals(200L, built.getId().longValue());
        assertEquals("Table", built.getName());
        assertEquals("Wooden table", built.getDescription());
        assertFalse(built.isAvailable());
        assertEquals(owner, built.getOwner());
        assertEquals(300L, built.getRequestId().longValue());
    }

    @Test
    void testNoArgsConstructor_ShouldInitializeDefaultValues() {
        Item defaultItem = new Item();

        assertNull(defaultItem.getId());
        assertNull(defaultItem.getName());
        assertNull(defaultItem.getDescription());
        assertFalse(defaultItem.isAvailable());
        assertNull(defaultItem.getOwner());
        assertNull(defaultItem.getRequestId());
    }
}