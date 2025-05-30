package ru.practicum.shareit.booking.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {

    public static BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .booker(booking.getBroker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .startTime(bookingDto.getStart())
                .endTime(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto mapToDtoFromNewRequest(NewBookingRequest request) {
        return BookingDto.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .build();
    }


}
