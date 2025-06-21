package ru.practirum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practirum.shareit.request.dto.NewItemRequest;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody NewItemRequest requestDto) {
        log.info("POST запрос вещи от пользователя с ID {}", userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET запрос на получение списка своих запросов вместе с данными об ответах на них.");
        return requestClient.findUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET запрос на получение списка запросов, созданных другими пользователями.");
        return requestClient.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable Long requestId) {
        log.info("GET запрос на получение данные об одном конкретном запросе c ID {}", requestId);
        return requestClient.findRequestById(requestId);
    }

}
