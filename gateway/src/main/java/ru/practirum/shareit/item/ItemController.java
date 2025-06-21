package ru.practirum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practirum.shareit.item.comment.CommentDto;
import ru.practirum.shareit.item.dto.ItemDto;
import ru.practirum.shareit.item.dto.UpdateItem;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей с userId: {}", userId);
        return itemClient.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long id) {
        log.info("Получение вещи с id: {},пользователем: {}", id, userId);
        return itemClient.findItemById(id,userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,@RequestParam String text) {
        log.info("Запрос на поиск вещи по тексту {}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Запос на создание вещи: {},пользователем: {}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария вещи: {},пользователем с id: {}", itemId, userId);
        return itemClient.createComment(userId,commentDto,itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody UpdateItem itemRequest, @PathVariable Long itemId) {
        log.info("Запрос на обновление вещи с id: {}", itemId);
        return itemClient.update(userId,itemId,itemRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        log.info("Удаление вещи с id: {}", id);
        return itemClient.deleteById(id);
    }
}
