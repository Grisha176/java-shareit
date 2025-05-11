package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public Item createItem(Item item) {
        item.setId(generatedId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values().stream().toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    private Long generatedId() {
        return idCounter.incrementAndGet();
    }

}
