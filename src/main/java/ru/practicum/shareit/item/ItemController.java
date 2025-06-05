package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей с userId: {}", userId);
        return itemService.getAllItems(userId).stream().toList();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long id) {
        log.info("Получение вещи с id: {},пользователем: {}", id, userId);
        return itemService.getItemById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Запрос на поиск вещи по тексту {}", text);
        return itemService.search(text).stream().toList();
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Запос на создание вещи: {},пользователем: {}", itemDto, userId);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария вещи: {},пользователем с id: {}", itemId, userId);
        commentDto.setItemId(itemId);
        return itemService.addComment(userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody UpdateItemRequest itemRequest, @PathVariable Long itemId) {
        log.info("Запрос на обновление вещи с id: {}", itemId);
        return itemService.updateItem(itemId, userId, itemRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        log.info("Удаление вещи с id: {}", id);
        return new ResponseEntity<>("{ \"Удаление прошло успешно!\" }", HttpStatus.OK);
    }

}
