package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceInMemory implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceInMemory(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    public ItemDto createItem(Long userId, Item item) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        item.setOwnerId(userId);
        item = itemRepository.createItem(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updatedItem) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        item = ItemMapper.updateItemFields(item, updatedItem);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemRepository.getAllItems().stream().filter(item -> item.getOwnerId().equals(userId)).map(item -> ItemMapper.mapToItemDto(item)).toList();

    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.mapToItemDto(itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена")));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase();

        return itemRepository.getAllItems().stream()
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText) ||
                                item.getDescription().toLowerCase().contains(searchText))
                .filter(item -> item.getAvailable().equals(Boolean.TRUE))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
