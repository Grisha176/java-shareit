package bookingtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class NewBookingRequestTest {

    @Autowired
    private JacksonTester<NewBookingRequest> json;

    private NewBookingRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alice", "alice@example.com");

        request = NewBookingRequest.builder()
                .start(LocalDateTime.of(2020, 12, 1, 12, 20))
                .end(LocalDateTime.of(2020, 12, 2, 12, 20))
                .itemId(1L)
                .broker(user.getId())
                .build();
    }

    @Test
    void testDeserializeBookingDto() throws IOException {
        String content = "{\"start\":\"2020-12-01T12:20:00\",\"end\":\"2020-12-02T12:20:00\",\"itemId\":1,\"broker\":1}";

        NewBookingRequest request1 = json.parse(content).getObject();

        assertNotNull(request1);
        assertEquals(request.getStart(), request1.getStart());
        assertEquals(request.getItemId(), request1.getItemId());
        assertEquals(request.getEnd(), request1.getEnd());
        assertEquals(request.getBroker(), request1.getBroker());
    }

    @Test
    void testSerializeNewBookingRequest() throws IOException {

        String bookingDto = json.write(request).getJson();
        NewBookingRequest deserializedRequest = json.parse(bookingDto).getObject();

        assertEquals(request, deserializedRequest);
    }

    @Test
    void testDeserializeBookingDtoWithNullValues() throws IOException {

        String content = "{\"start\":null,\"end\":null,\"itemId\":null,\"booker\":null}";
        NewBookingRequest bookingDto = json.parse(content).getObject();

        assertNotNull(bookingDto, "Объект BookingDto не должен быть null");
        assertNull(bookingDto.getStart(), "Start должен быть null");
        assertNull(bookingDto.getEnd(), "End должен быть null");
        assertNull(bookingDto.getItemId(), "Item должен быть null");
        assertNull(bookingDto.getBroker(), "Booker должен быть null");
    }


}
