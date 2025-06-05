package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;
    @NotNull(message = "имя не может быть пустым")
    @NotBlank
    private String name;
    @NotNull(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
    private String request;
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

}
