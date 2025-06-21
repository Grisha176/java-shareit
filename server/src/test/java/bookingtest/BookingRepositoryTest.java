package bookingtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Создаём пользователей
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        // Создаём вещь
        item = new Item();
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);

        entityManager.flush();
    }

    @Test
    void findAllByBookerId_ReturnsAllBookingsForUser() {
        // Given
        Booking booking1 = createBooking(booker, item, now.plusDays(1), now.plusDays(2));
        Booking booking2 = createBooking(booker, item, now.plusDays(3), now.plusDays(4));
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId());

        // Then
        assertThat(bookings).hasSize(2)
                .contains(booking1, booking2);
    }

    @Test
    void findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore_ReturnsPastBookings() {
        // Given
        Booking pastBooking = createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        Booking futureBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2));
        entityManager.persist(pastBooking);
        entityManager.persist(futureBooking);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore(
                booker.getId(), now, now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(pastBooking);
    }

    @Test
    void findAllByBookerIdAndEndTimeIsBefore_ReturnsFinishedBookings() {
        // Given
        Booking finished = createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        Booking ongoing = createBooking(booker, item, now.minusDays(1), now.plusDays(1));
        entityManager.persist(finished);
        entityManager.persist(ongoing);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndTimeIsBefore(booker.getId(), now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(finished);
    }

    @Test
    void findAllByBookerIdAndStartTimeIsAfter_ReturnsFutureBookings() {
        // Given
        Booking future = createBooking(booker, item, now.plusDays(1), now.plusDays(2));
        Booking past = createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        entityManager.persist(future);
        entityManager.persist(past);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartTimeIsAfter(booker.getId(), now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(future);
    }

    @Test
    void findAllByBookerIdAndStatus_ReturnsFilteredBookings() {
        // Given
        Booking waiting = createBooking(booker, item, now.plusDays(1), now.plusDays(2));
        waiting.setStatus(BookingStatus.WAITING);
        Booking approved = createBooking(booker, item, now.plusDays(3), now.plusDays(4));
        approved.setStatus(BookingStatus.APPROVED);
        entityManager.persist(waiting);
        entityManager.persist(approved);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.APPROVED);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(approved);
    }

    @Test
    void findAllByItemOwnerId_ReturnsAllBookingsForOwnerItems() {
        // Given
        Item anotherItem = Item.builder()
                .name("Saw")
                .description("Hand saw")
                .available(true)
                .owner(booker)
                .build();
        entityManager.persist(anotherItem);

        Booking bookingForMyItem = createBooking(owner, item, now.plusDays(1), now.plusDays(2));
        Booking bookingForOtherItem = createBooking(owner, anotherItem, now.plusDays(3), now.plusDays(4));
        entityManager.persist(bookingForMyItem);
        entityManager.persist(bookingForOtherItem);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(owner.getId());

        // Then
        assertThat(bookings).hasSize(1)
                .contains(bookingForMyItem);
    }

    @Test
    void findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore_ReturnsPastBookingsForOwner() {
        // Given
        Booking past = createBooking(owner, item, now.minusDays(2), now.minusDays(1));
        Booking future = createBooking(owner, item, now.plusDays(1), now.plusDays(2));
        entityManager.persist(past);
        entityManager.persist(future);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore(
                owner.getId(), now, now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(past);
    }

    @Test
    void findAllByItemOwnerIdAndEndTimeIsBefore_ReturnsFinishedBookingsForOwner() {
        // Given
        Booking finished = createBooking(owner, item, now.minusDays(2), now.minusDays(1));
        Booking ongoing = createBooking(owner, item, now.minusDays(1), now.plusDays(1));
        entityManager.persist(finished);
        entityManager.persist(ongoing);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndTimeIsBefore(owner.getId(), now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(finished);
    }

    @Test
    void findAllByItemOwnerIdAndStartTimeIsAfter_ReturnsFutureBookingsForOwner() {
        // Given
        Booking future = createBooking(owner, item, now.plusDays(1), now.plusDays(2));
        Booking past = createBooking(owner, item, now.minusDays(2), now.minusDays(1));
        entityManager.persist(future);
        entityManager.persist(past);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartTimeIsAfter(owner.getId(), now);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(future);
    }

    @Test
    void findAllByItemOwnerIdAndStatus_ReturnsFilteredBookingsForOwner() {
        // Given
        Booking approved = createBooking(owner, item, now.plusDays(1), now.plusDays(2));
        approved.setStatus(BookingStatus.APPROVED);
        Booking waiting = createBooking(owner, item, now.plusDays(3), now.plusDays(4));
        waiting.setStatus(BookingStatus.WAITING);
        entityManager.persist(approved);
        entityManager.persist(waiting);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(owner.getId(), BookingStatus.APPROVED);

        // Then
        assertThat(bookings).hasSize(1)
                .contains(approved);
    }

    @Test
    void findLastFinishedBookingByItem_ReturnsLastFinishedBooking() {
        // Given
        Booking b1 = createBooking(owner, item, now.minusDays(5), now.minusDays(3));
        b1.setStatus(BookingStatus.APPROVED);
        Booking b2 = createBooking(owner, item, now.minusDays(2), now.minusDays(1));
        b2.setStatus(BookingStatus.APPROVED);
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.flush();

        // When
        Optional<Booking> lastBooking = bookingRepository.findLastFinishedBookingByItem(item);

        // Then
        assertTrue(lastBooking.isPresent());
        assertThat(lastBooking.get()).isEqualTo(b2);
    }

    @Test
    void findNextBookingByItem_ReturnsFirstUpcomingBooking() {
        // Given
        Booking b1 = createBooking(owner, item, now.plusDays(1), now.plusDays(2));
        b1.setStatus(BookingStatus.APPROVED);
        entityManager.persist(b1);
        entityManager.flush();

        // When
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItem(item);

        // Then
        assertTrue(nextBooking.isPresent());
        assertThat(nextBooking.get()).isEqualTo(b1);
    }

    @Test
    void findAllByUserBookings_ReturnsApprovedBookingsBeforeNow() {
        // Given
        Booking b1 = createBooking(booker, item, now.minusDays(2), now.minusDays(1));
        b1.setStatus(BookingStatus.APPROVED);
        Booking b2 = createBooking(booker, item, now.minusDays(4), now.minusDays(3));
        b2.setStatus(BookingStatus.APPROVED);
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.flush();

        // When
        List<Booking> bookings = bookingRepository.findAllByUserBookings(booker.getId(), item.getId(), now);

        // Then
        assertThat(bookings).hasSize(2)
                .contains(b1, b2);
    }

    // Вспомогательный метод
    private Booking createBooking(User user, Item item, LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .booker(user)
                .item(item)
                .startTime(start)
                .endTime(end)
                .status(BookingStatus.WAITING)
                .build();
    }
}