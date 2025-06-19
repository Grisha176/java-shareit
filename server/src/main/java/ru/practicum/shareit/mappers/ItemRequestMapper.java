package ru.practicum.shareit.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @BeanMapping(ignoreByDefault = true)
    ItemRequest mapToItemRequest(NewItemRequestDto requestDto, @Param("requestorId") Long requestorId);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "created",source = "createdTime")
    ItemRequestDto mapToDto(ItemRequest itemRequest);

}
