package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody NewItemRequestDto itemRequestCreateDto) {
        log.info("POST запрос вещи от пользователя с ID {}", userId);
        return itemRequestService.addNewRequest(userId, itemRequestCreateDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {
        log.info("GET запрос на получение данные об одном конкретном запросе c ID {}", requestId);
        return itemRequestService.getById(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET запрос на получение списка запросов, созданных другими пользователями.");
        return itemRequestService.getAllRequests();
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET запрос на получение списка своих запросов вместе с данными об ответах на них.");
        return itemRequestService.getByRequestorId(userId);
    }
}
