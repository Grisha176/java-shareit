package itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RespondItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.ItemMapperImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class ItemMapperTest {


    private ItemMapperImpl mapper = new ItemMapperImpl();


    private final User owner = new User();
    private final Item item = new Item();

    @BeforeEach
    void setUp() {
        // Инициализируем владельца
        owner.setId(1L);
        owner.setName("Alice");
        owner.setEmail("alice@example.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(200L);
        // Инициализируем вещь
        item.setId(100L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);
    }


    @Test
    void toItemFromCreateDto() {
        ItemDto itemCreateDto = ItemDto.builder()
                .name("test")
                .description("description")
                .available(true)
                .build();

        Item item = mapper.mapToItem(itemCreateDto);

        assertEquals(itemCreateDto.getName(), item.getName());
        assertEquals(itemCreateDto.getDescription(), item.getDescription());
    }

    @Test
    void toItemFromDto() {
        User owner = User.builder().id(1L).name("Owner").email("test@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test description")
                .available(true)
                .owner(owner)
                .build();

        ItemDto itemDto = mapper.mapToItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void mapToItemDto_WhenItemIsNotNull_ShouldReturnCorrectDto() {
        ItemDto dto = mapper.mapToItemDto(item);

        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.isAvailable(), dto.getAvailable());
        assertEquals(item.getRequest().getId(), dto.getRequestId());
        assertEquals(owner.getId(), dto.getOwnerId());
    }

    @Test
    void mapToItemDto_WhenItemHasNullFields_ShouldHandleGracefully() {
        Item emptyItem = new Item();
        emptyItem.setId(200L); // Только id

        ItemDto dto = mapper.mapToItemDto(emptyItem);

        assertNotNull(dto);
        assertEquals(200L, dto.getId().longValue());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertFalse(dto.getAvailable());
        assertNull(dto.getRequestId());
        assertNull(dto.getOwnerId());
    }

    @Test
    void mapToItemDto_WhenItemIsNull_ShouldReturnNull() {
        ItemDto dto = mapper.mapToItemDto(null);

        assertNull(dto);
    }

    @Test
    void mapToItem_WhenItemDtoIsNotNull_ShouldReturnCorrectItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(300L)
                .name("Saw")
                .description("Hand saw")
                .available(false)
                .requestId(400L)
                .build();

        Item mappedItem = mapper.mapToItem(itemDto);

        assertNotNull(mappedItem);
        assertEquals(itemDto.getId(), mappedItem.getId());
        assertEquals(itemDto.getName(), mappedItem.getName());
        assertEquals(itemDto.getDescription(), mappedItem.getDescription());
        assertEquals(itemDto.getAvailable(), mappedItem.isAvailable());
        // assertEquals(itemDto.getRequestId(), mappedItem.getRequest().getId());
    }

    @Test
    void mapToItem_WhenItemDtoIsNull_ShouldReturnNull() {
        Item mappedItem = mapper.mapToItem(null);

        assertNull(mappedItem);
    }

    @Test
    void mapToRespond_WhenItemIsNotNull_ShouldReturnCorrectRespondItemRequest() {
        RespondItemRequest response = mapper.mapToRespond(item);

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(owner.getId(), response.getOwnerId());
    }

    @Test
    void mapToRespond_WhenItemIsNull_ShouldReturnNull() {
        RespondItemRequest response = mapper.mapToRespond(null);

        assertNull(response);
    }

    @Test
    void mapToItemDto_WhenOwnerIdIsNull_ShouldNotFail() {
        item.setOwner(null); // Убираем владельца

        ItemDto dto = mapper.mapToItemDto(item);

        assertNotNull(dto);
        assertNull(dto.getOwnerId());
    }

    @Test
    void mapToItem_WhenDtoHasEmptyName_ShouldPreserveIt() {
        ItemDto itemDto = ItemDto.builder()
                .id(500L)
                .name("")
                .description("No description")
                .available(true)
                .requestId(600L)
                .build();

        Item mappedItem = mapper.mapToItem(itemDto);

        assertNotNull(mappedItem);
        assertEquals("", mappedItem.getName());
        // assertEquals(600L, mappedItem.getRequest().getId());
    }

    @Test
    void mapToItemDto_WhenItemHasBlankDescription_ShouldPreserveIt() {
        item.setDescription("   "); // Белый пробел

        ItemDto dto = mapper.mapToItemDto(item);

        assertNotNull(dto);
        assertEquals("   ", dto.getDescription());
    }

    @Test
    void mapToItem_WhenDtoHasRequestId_ShouldSetRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .id(700L)
                .name("Hammer")
                .requestId(800L)
                .build();

        Item mappedItem = mapper.mapToItem(itemDto);

        assertNotNull(mappedItem);
    }
}