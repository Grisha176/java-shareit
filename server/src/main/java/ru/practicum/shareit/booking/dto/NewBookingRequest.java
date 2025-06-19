package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class NewBookingRequest {

    private LocalDateTime start;
    private LocalDateTime end;
    private Long broker;
    private Long itemId;
}
