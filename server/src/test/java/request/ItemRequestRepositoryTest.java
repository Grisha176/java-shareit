package request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequesterIdWhenRequestsExistForUserThenReturnListOfRequests() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        ItemRequest request1 = new ItemRequest();
        request1.setRequestorId(user.getId());
        request1.setDescription("Description 1");
        request1.setCreatedTime(LocalDateTime.now());
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequestorId(user.getId());
        request2.setDescription("Description 2");
        request2.setCreatedTime(LocalDateTime.now());
        entityManager.persist(request2);

        User otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        entityManager.persist(otherUser);

        ItemRequest request3 = new ItemRequest();
        request3.setRequestorId(otherUser.getId());
        request3.setDescription("Description 3");
        request3.setCreatedTime(LocalDateTime.now());
        entityManager.persist(request3);

        entityManager.flush();
        entityManager.clear();

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user.getId());

        assertEquals(2, requests.size());
        assertTrue(requests.stream().allMatch(r -> r.getRequestorId().equals(user.getId())));
    }

    @Test
    void findAllByRequesterId_whenNoRequestsExistForUser_thenReturnEmptyList() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        entityManager.flush();
        entityManager.clear();
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user.getId());

        assertTrue(requests.isEmpty());
    }

    @Test
    void findAllByRequesterId_whenUserDoesNotExist_thenReturnEmptyList() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(999L);
        assertTrue(requests.isEmpty());
    }
}
