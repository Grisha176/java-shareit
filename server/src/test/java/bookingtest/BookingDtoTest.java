package bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto dto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alice", "alice@example.com");

        dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2020, 12, 1, 12, 20))
                .end(LocalDateTime.of(2020, 12, 2, 12, 20))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(new Item(1L, "Drill", "Powerful drill", true, user, 2L))
                .build();
    }

    @Test
    void testDeserializeBookingDto() throws IOException {
        String content = "{\"id\":1,\"start\":\"2020-12-01T12:20:00\",\"end\":\"2020-12-02T12:20:00\",\"item\":{},\"booker\":{},\"status\":\"WAITING\"}";

        BookingDto bookingDto = json.parse(content).getObject();

        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(dto.getStart(), bookingDto.getStart());
        assertEquals(dto.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testSerializeBookingDto() throws IOException {

        String bookingDto = json.write(dto).getJson();
        BookingDto deserializedBooking = json.parse(bookingDto).getObject();

        assertEquals(dto, deserializedBooking);
    }

    @Test
    void testDeserializeBookingDtoWithNullValues() throws IOException {

        String content = "{\"id\":null,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":null}";
        BookingDto bookingDto = json.parse(content).getObject();

        assertNotNull(bookingDto, "Объект BookingDto не должен быть null");
        assertNull(bookingDto.getId(), "ID должен быть null");
        assertNull(bookingDto.getStart(), "Start должен быть null");
        assertNull(bookingDto.getEnd(), "End должен быть null");
        assertNull(bookingDto.getItem(), "Item должен быть null");
        assertNull(bookingDto.getBooker(), "Booker должен быть null");
        assertNull(bookingDto.getStatus(), "Status должен быть null");
    }


}
