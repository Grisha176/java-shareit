package itemtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Item item;
    private User user;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        // Сохраняем пользователя через EntityManager
        user = entityManager.persist(user);

        // Создаем вещь
        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);

        // Сохраняем вещь
        item = entityManager.persist(item);

        // Создаем комментарии
        comment1 = new Comment();
        comment1.setComment("Great tool!");
        comment1.setItem(item);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now().minusDays(1));

        comment2 = new Comment();
        comment2.setComment("Very useful");
        comment2.setItem(item);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());

        // Сохраняем комментарии
        comment1 = entityManager.persist(comment1);
        comment2 = entityManager.persist(comment2);
    }

    @Test
    void findAllByItem_WhenItemHasComments_ShouldReturnAllCommentsForItem() {
        // When
        List<Comment> comments = commentRepository.findAllByItem(item);

        // Then
        assertNotNull(comments);
        assertThat(comments).hasSize(2)
                .contains(comment1, comment2);
    }

    @Test
    void findAllByItem_WhenItemHasNoComments_ShouldReturnEmptyList() {
        // Given
        Item otherItem = new Item();
        otherItem.setName("Saw");
        otherItem.setDescription("Hand saw");
        otherItem.setAvailable(true);
        otherItem.setOwner(user);

        otherItem = entityManager.persist(otherItem);

        // When
        List<Comment> comments = commentRepository.findAllByItem(otherItem);

        // Then
        assertTrue(comments.isEmpty());
    }
}
