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

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BookingMapperTest {


    private final BookingMapper bookingMapper = new BookingMapperImpl();

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


}
