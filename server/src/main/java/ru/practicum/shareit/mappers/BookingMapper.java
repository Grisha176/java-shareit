package ru.practicum.shareit.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "start", source = "startTime")
    @Mapping(target = "end", source = "endTime")
    BookingDto mapToDto(Booking booking);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startTime", source = "request.start")
    @Mapping(target = "endTime", source = "request.end")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    Booking mapToBooking(@Param("request") NewBookingRequest request,
                         @Param("item") Item item,
                         @Param("user") User user);
}
