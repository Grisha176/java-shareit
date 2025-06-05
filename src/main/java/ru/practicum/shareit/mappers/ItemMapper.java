package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    ItemDto mapToItemDto(Item item);
    Item mapToItem(ItemDto itemDto);
}
