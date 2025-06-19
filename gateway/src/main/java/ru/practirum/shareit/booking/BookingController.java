package ru.practirum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practirum.shareit.booking.dto.BookingState;
import ru.practirum.shareit.booking.dto.NewBookingRequest;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid @RequestBody NewBookingRequest request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        request.setBroker(userId);
        log.info("Запрос на добавление бронирования");
        return bookingClient.create(userId,request);
    }

    @PatchMapping("/{bookingsId}")
    public ResponseEntity<Object> respondToBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingsId") Long bookingId, @RequestParam(value = "approved") Boolean approve) throws JsonProcessingException {
        log.info("Ответ на бронирование с id: {}",bookingId);
        return bookingClient.update(userId, bookingId, approve);

    }

    @GetMapping("/{bookingsId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingsId") Long bookingId) {
        log.info("Запрос на получение бронирования с id: {}",bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(value = "state", defaultValue = "ALL") String status) {
        try {
            BookingState state = BookingState.valueOf(status);
            log.info("Запрос на получение бронирований,userId: {}",userId);
            return bookingClient.getBookings(userId,state);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
