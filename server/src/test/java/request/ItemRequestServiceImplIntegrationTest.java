package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // Очистка перед каждым тестом
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем пользователя и вещь
        owner = new User(null, "John", "john@example.com");
        owner = userRepository.save(owner);

        item = new Item();
        item.setName("Drill");
        item.setDescription("A powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(null);
        item = itemRepository.save(item);

        // Комментарий к вещи
        comment = new Comment();
        comment.setComment("Great tool!");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    // --- createItem ---

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_createItem() {
        assertThatThrownBy(() -> {
            throw new NotFoundException("Пользователь не найден");
        }).isInstanceOf(NotFoundException.class);
    }

    // --- deleteItem ---
    @Test
    void shouldDeleteItem_whenExists() {
        Long itemId = item.getId();

        itemRepository.deleteById(itemId);

        assertThat(itemRepository.findById(itemId)).isEmpty();
    }

    // --- updateItem ---
    @Test
    void shouldUpdateItemFields_whenValidInput() {
        String newName = "Updated Drill";
        String newDescription = "Updated description";
        boolean newAvailable = false;

        Item existingItem = itemRepository.findById(item.getId()).orElseThrow();
        existingItem.setName(newName);
        existingItem.setDescription(newDescription);
        existingItem.setAvailable(newAvailable);

        Item updatedItem = itemRepository.save(existingItem);

        assertThat(updatedItem.getName()).isEqualTo(newName);
        assertThat(updatedItem.getDescription()).isEqualTo(newDescription);
    }

    // --- getAllItems ---
    @Test
    void shouldReturnAllItemsForOwner() {
        List<Item> items = itemRepository.findItemByOwnerId(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Drill");
    }

    // --- getItemById ---

    @Test
    void shouldSearchItemsByNameOrDescriptionIgnoreCaseAndAvailableTrue() {
        Item anotherItem = new Item();
        anotherItem.setName("Laptop");
        anotherItem.setDescription("Gaming laptop");
        anotherItem.setAvailable(true);
        anotherItem.setOwner(owner);
        itemRepository.save(anotherItem);

        List<Item> result = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue("drill", "drill");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }


    // --- addComment ---
    @Test
    void shouldAddComment_whenUserHasPastBookings() {
        Comment newComment = new Comment();
        newComment.setComment("Excellent!");
        newComment.setItem(item);
        newComment.setAuthor(owner);
        newComment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(newComment);

        assertThat(savedComment.getComment()).isEqualTo("Excellent!");
        assertThat(savedComment.getItem().getId()).isEqualTo(item.getId());
    }

    // --- getByRequestId ---

    @Test
    void shouldReturnEmptyList_whenNoItemsWithGivenRequestId() {
        List<Item> result = itemRepository.findAllByRequestId(999L);

        assertThat(result).isEmpty();
    }
}

