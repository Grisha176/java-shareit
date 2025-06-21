package bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookingServiceImpl.class})
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class BookingServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper; // можно заменить на реальный маппер, если он есть

    private User booker;
    private User owner;
    private Item item;
    private LocalDateTime now;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();

        booking = new Booking();
        booking.setId(500L);
        booking.setStartTime(now.plusDays(1));
        booking.setEndTime(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        // Dto
        bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStartTime());
        bookingDto.setEnd(booking.getEndTime());
        bookingDto.setStatus(booking.getStatus());
        // Создаем пользователей
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        // Создаем вещь
        item = new Item();
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }


    @Test
    void getBookingById_WhenUserIsNotBookerOrOwner_ShouldThrowValidationException() {
        Booking booking = createAndSaveBooking();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(999L, booking.getId()));

        assertTrue(exception.getMessage().contains("не является: пользователем вещи или тем кто забронировал вещь"));
    }

    @Test
    void getAllBooking_WhenStateAll_ShouldReturnAllForBooker() {
        createAndSaveBooking();
        List<BookingDto> bookings = bookingService.getAllBooking(booker.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertThat(bookings).hasSize(1);
    }





    @Test
    void respondToBooking_WhenUserIsNotOwner_ShouldThrowAccessException() {
        Booking waitingBooking = Booking.builder()
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        waitingBooking = bookingRepository.save(waitingBooking);
        Long id = waitingBooking.getId();

        assertThrows(ValidationException.class,
                () -> bookingService.respondToBooking(999L, id, true));

    }

    @Test
    void getAllItemBooking_WhenStateAll_ShouldReturnAllBookingsForOwner() {
        Booking b1 = Booking.builder()
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        bookingRepository.save(b1);

        List<BookingDto> result = bookingService.getAllItemBooking(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void getAllItemBooking_WhenStateWaiting_ShouldReturnOnlyWaitingBookings() {
        Booking b1 = Booking.builder()
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        bookingRepository.save(b1);

        List<BookingDto> result = bookingService.getAllItemBooking(owner.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    // Вспомогательный метод для создания бронирования
    private Booking createAndSaveBooking() {
        Booking booking = Booking.builder()
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();

        return bookingRepository.save(booking);
    }
}