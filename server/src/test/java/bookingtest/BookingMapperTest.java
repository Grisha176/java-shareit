package bookingtest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.mappers.BookingMapperImpl;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class BookingMapperTest {


    private final BookingMapper bookingMapper = new BookingMapperImpl();



    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void mapToDto_WhenBookingIsNull_ShouldReturnNull() {
        // When
        BookingDto result = bookingMapper.mapToDto(null);

        // Then
        assertNull(result);
    }
    @Test
    void mapToBooking() {

        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 1, 21, 11, 11))
                .itemId(1L)
                .broker(1L)
                .build();

        User booker = User.builder()
                .id(1L)
                .email("example.email.ru")
                .name("userName")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .owner(booker)
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 1, 21, 11, 11))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();


        Booking booking = bookingMapper.mapToBooking(newBookingRequest, item, booker);


        assertEquals(newBookingRequest.getStart(), booking.getStartTime());
        assertEquals(newBookingRequest.getEnd(), booking.getEndTime());
        assertEquals(newBookingRequest.getItemId(), booking.getItem().getId());
        assertEquals(newBookingRequest.getBroker(), booking.getBooker().getId());

    }

    @Test
    void mapToDto() {

        User booker = User.builder()
                .id(1L)
                .email("example.email.ru")
                .name("userName")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .owner(booker)
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2000, 1, 20, 11, 11))
                .endTime(LocalDateTime.of(2000, 1, 21, 11, 11))
                .item(item)
                .booker(booker)
                .build();

        BookingDto bookingDto = bookingMapper.mapToDto(booking);


        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStartTime(), bookingDto.getStart());
        assertEquals(booking.getEndTime(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());


    }

    @Test
    void mapToDto_WhenBookingIsNotNull_ShouldMapAllFields() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        Item item = new Item();
        item.setId(100L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(500L);
        booking.setStartTime(now.minusDays(1));
        booking.setEndTime(now.plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        // When
        BookingDto dto = bookingMapper.mapToDto(booking);

        // Then
        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStartTime(), dto.getStart());
        assertEquals(booking.getEndTime(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());

        assertNotNull(dto.getItem());
        assertEquals(item.getId(), dto.getItem().getId());
        assertNotNull(dto.getBooker());
        assertEquals(user.getId(), dto.getBooker().getId());
    }

    @Test
    void mapToBooking_WhenRequestAndEntitiesAreProvided_ShouldCreateBooking() {
        // Given
        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(100L)
                .broker(1L)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .build();

        User user = new User();
        user.setId(1L);
        user.setName("Bob");
        user.setEmail("bob@example.com");

        Item item = new Item();
        item.setId(100L);
        item.setName("Saw");
        item.setAvailable(true);

        // When
        Booking booking = bookingMapper.mapToBooking(request, item, user);

        // Then
        assertNotNull(booking);
        assertEquals(request.getStart(), booking.getStartTime());
        assertEquals(request.getEnd(), booking.getEndTime());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
    }

    @Test
    void mapToBooking_WhenOnlyItemAndUserProvided_ShouldSetDefaultStatus() {
        // Given
        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(100L)
                .broker(1L)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .build();

        Item item = new Item();
        item.setId(100L);
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        // When
        Booking booking = bookingMapper.mapToBooking(request, item, user);
        booking.setStatus(BookingStatus.WAITING);

        // Then
        assertNotNull(booking);
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void mapToBooking_WhenRequestIsNull_ShouldStillMapItemAndUser() {
        // Given
        Item item = new Item();
        item.setId(100L);

        User user = new User();
        user.setId(1L);

        // When
        Booking booking = bookingMapper.mapToBooking(null, item, user);

        // Then
        assertNotNull(booking);
        assertNull(booking.getStartTime());
        assertNull(booking.getEndTime());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
    }

    @Test
    void mapToBooking_WhenRequestHasBlankTime_ShouldMapEmptyBooking() {
        // Given
        Item item = new Item();
        item.setId(100L);

        User user = new User();
        user.setId(1L);

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(100L)
                .broker(1L)
                .build(); // start и end == null

        // When
        Booking booking = bookingMapper.mapToBooking(request, item, user);

        // Then
        assertNotNull(booking);
        assertNull(booking.getStartTime());
        assertNull(booking.getEndTime());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
    }

    @Test
    void mapToDto_WhenBookingHasNullFields_ShouldHandleGracefully() {
        // Given
        Booking booking = new Booking();
        booking.setId(600L);
        booking.setStatus(BookingStatus.APPROVED);
        // startTime, endTime, booker, item == null

        // When
        BookingDto dto = bookingMapper.mapToDto(booking);

        // Then
        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertNull(dto.getBooker());
        assertNull(dto.getItem());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    void mapToBooking_WhenRequestHasFutureDates_ShouldMapCorrectly() {
        // Given
        Item item = new Item();
        item.setId(100L);

        User user = new User();
        user.setId(1L);

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(item.getId())
                .broker(user.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        // When
        Booking booking = bookingMapper.mapToBooking(request, item, user);

        // Then
        assertNotNull(booking);
        assertEquals(request.getStart(), booking.getStartTime());
        assertEquals(request.getEnd(), booking.getEndTime());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
    }


}
