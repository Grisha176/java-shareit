package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "start", source = "startTime")
    @Mapping(target = "end", source = "endTime")
    BookingDto mapToDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startTime", source = "request.start")
    @Mapping(target = "endTime", source = "request.end")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    Booking mapToBooking(@Param("request") NewBookingRequest request,
                         @Param("item") Item item,
                         @Param("user") User user);
}
