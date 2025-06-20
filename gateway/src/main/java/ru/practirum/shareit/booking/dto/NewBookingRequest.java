package ru.practirum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingRequest {

    @NotNull
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    private Long broker;
    @NotNull
    private Long itemId;

}
