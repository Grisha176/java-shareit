package bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
class BookingTest {

    @Autowired
    private JacksonTester<Booking> jacksonTester;

    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Инициализируем пользователя
        booker = new User();
        booker.setId(1L);
        booker.setName("Alice");
        booker.setEmail("alice@example.com");

        // Инициализируем вещь
        item = new Item();
        item.setId(100L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(booker);

        // Подготавливаем бронирование
        booking = Booking.builder()
                .id(200L)
                .startTime(now.minusHours(1))
                .endTime(now.plusHours(1))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    private Booking booking;

    @Test
    void getters_ShouldReturnCorrectValues() {
        assertNotNull(booking.getId());
        assertEquals(now.minusHours(1), booking.getStartTime());
        assertEquals(now.plusHours(1), booking.getEndTime());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void setters_ShouldUpdateFields() {
        LocalDateTime newStart = now.plusDays(1);
        LocalDateTime newEnd = now.plusDays(2);

        booking.setId(300L);
        booking.setStartTime(newStart);
        booking.setEndTime(newEnd);
        booking.setStatus(BookingStatus.APPROVED);

        assertEquals(300L, booking.getId().longValue());
        assertEquals(newStart, booking.getStartTime());
        assertEquals(newEnd, booking.getEndTime());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        Booking b1 = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2025, 6, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 6, 2, 10, 0))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        Booking b2 = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2025, 6, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 6, 2, 10, 0))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        Booking b3 = Booking.builder()
                .id(2L)
                .startTime(LocalDateTime.of(2025, 6, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 6, 2, 10, 0))
                .booker(booker)
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();

        // Equals
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertNotEquals(b2, b3);

        // HashCode
        assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        assertThat(b1.hashCode()).isNotEqualTo(b3.hashCode());
    }


    @Test
    void serializeToJson_ShouldIncludeAllFields() throws IOException {
        var jsonContent = jacksonTester.write(booking);

        assertThat(jsonContent).hasJsonPath("$.id", 200L);
        assertThat(jsonContent).hasJsonPath("$.startTime");
        assertThat(jsonContent).hasJsonPath("$.endTime");
        assertThat(jsonContent).hasJsonPath("$.booker.id", 1L);
        assertThat(jsonContent).hasJsonPath("$.item.id", 100L);
        assertThat(jsonContent).hasJsonPath("$.status", "WAITING");
    }

    @Test
    void deserializeFromJson_ShouldPreserveAllFields() throws IOException {
        String content = String.format(
                "{ \"id\": 400, \"startTime\": \"%s\", \"endTime\": \"%s\", " +
                        "\"booker\": { \"id\": 1, \"name\": \"Bob\", \"email\": \"bob@example.com\" }, " +
                        "\"item\": { \"id\": 101, \"name\": \"Saw\", \"description\": \"Hand saw\", \"available\": true, " +
                        "  \"owner\": { \"id\": 2, \"name\": \"Owner\", \"email\": \"owner@example.com\" } }, " +
                        "\"status\": \"APPROVED\" }",
                now.minusMinutes(30), now.plusMinutes(30)
        );

        Booking parsedBooking = jacksonTester.parseObject(content);

        assertNotNull(parsedBooking);
        assertEquals(400L, parsedBooking.getId().longValue());
        assertEquals(BookingStatus.APPROVED, parsedBooking.getStatus());

        assertNotNull(parsedBooking.getBooker());
        assertEquals(1L, parsedBooking.getBooker().getId().longValue());
        assertEquals("Bob", parsedBooking.getBooker().getName());
        assertEquals("bob@example.com", parsedBooking.getBooker().getEmail());

        assertNotNull(parsedBooking.getItem());
        assertEquals(101L, parsedBooking.getItem().getId().longValue());
        assertEquals("Saw", parsedBooking.getItem().getName());
        assertTrue(parsedBooking.getItem().isAvailable());
    }

    @Test
    void builder_ShouldCreateBookingWithAllFields() {
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        Booking built = Booking.builder()
                .id(500L)
                .startTime(start)
                .endTime(end)
                .booker(booker)
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();

        assertNotNull(built);
        assertEquals(500L, built.getId().longValue());
        assertEquals(start, built.getStartTime());
        assertEquals(end, built.getEndTime());
        assertEquals(booker, built.getBooker());
        assertEquals(item, built.getItem());
        assertEquals(BookingStatus.REJECTED, built.getStatus());
    }

    @Test
    void noArgsConstructor_ShouldInitializeNullValues() {
        Booking empty = new Booking();

        assertNull(empty.getId());
        assertNull(empty.getStartTime());
        assertNull(empty.getEndTime());
        assertNull(empty.getBooker());
        assertNull(empty.getItem());
        assertNull(empty.getStatus());
    }
}