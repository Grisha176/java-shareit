package ru.practicum.shareit.booking.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(NewBookingRequest request);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBooking(Long userId, BookingState state);

    BookingDto respondToBooking(Long userId, Long bookingId, Boolean status) throws JsonProcessingException;

    List<BookingDto> getAllItemBooking(Long userId, BookingState state);

}
