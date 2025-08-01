package ru.practirum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practirum.shareit.booking.dto.BookingState;
import ru.practirum.shareit.booking.dto.NewBookingRequest;
import ru.practirum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, NewBookingRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> update(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);

    }

    public ResponseEntity<Object> getBookings(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllOwner(Long ownerId, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()

        );

        return get("/owner?state=", ownerId, parameters);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}
