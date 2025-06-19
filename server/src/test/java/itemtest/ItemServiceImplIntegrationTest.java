package itemtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createItemReturnItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto itemDto = itemService.createItem(savedUser.getId(), itemCreateDto);

        assertNotNull(itemDto.getId());
        assertEquals(itemCreateDto.getName(), itemDto.getName());
        assertEquals(itemCreateDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void getItemDtoByIdReturnItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto createdItemDto = itemService.createItem(savedUser.getId(), itemCreateDto);

        ItemDto itemDto = itemService.getItemById(createdItemDto.getId(), savedUser.getId());

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals(createdItemDto.getName(), itemDto.getName());
    }

    @Test
    void searchItemsReturnListOfItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemCreateDto = new ItemDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description with keyword");
        itemCreateDto.setAvailable(true);

        itemService.createItem(savedUser.getId(), itemCreateDto);

        List<ItemDto> searchResults = itemService.search("keyword").stream().toList();

        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
    }
}