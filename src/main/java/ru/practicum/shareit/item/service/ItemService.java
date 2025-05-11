package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    void deleteItem(Long itemId);

    ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updatedItem);

    Collection<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> search(String text);

}
