package ru.practicum.shareit.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RespondItemRequest;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "owner.id", target = "ownerId")
    ItemDto mapToItemDto(Item item);

    @BeanMapping(ignoreByDefault = true)
    Item mapToItem(ItemDto itemDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "owner.id", target = "ownerId")
    RespondItemRequest mapToRespond(Item item);
}
