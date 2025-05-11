package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    void deleteItem(Long itemId);

    Item updateItem(Item item);

    Collection<Item> getAllItems();

    Optional<Item> getItemById(Long itemId);


}
