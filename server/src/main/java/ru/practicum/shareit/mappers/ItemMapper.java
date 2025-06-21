package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RespondItemRequest;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    ItemDto mapToItemDto(Item item);

    Item mapToItem(ItemDto itemDto);

    @Mapping(source = "owner.id", target = "ownerId")
    RespondItemRequest mapToRespond(Item item);
}
