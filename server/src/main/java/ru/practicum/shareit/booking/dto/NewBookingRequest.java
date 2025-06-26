package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingRequest {

    private LocalDateTime start;
    private LocalDateTime end;
    private Long broker;
    private Long itemId;
}
