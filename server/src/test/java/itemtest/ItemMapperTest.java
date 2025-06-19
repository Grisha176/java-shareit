package itemtest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.ItemMapperImpl;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class ItemMapperTest {


    private ItemMapperImpl mapper = new ItemMapperImpl();

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
}