package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long brokerId;
    private Long itemId;
    private BookingStatus status;
}

