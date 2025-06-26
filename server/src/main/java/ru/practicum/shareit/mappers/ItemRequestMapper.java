package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemRequestMapper {

    ItemRequest mapToItemRequest(NewItemRequestDto requestDto, @Param("requestorId") Long requestorId);

    @Mapping(target = "created",source = "createdTime")
    ItemRequestDto mapToDto(ItemRequest itemRequest);


}
