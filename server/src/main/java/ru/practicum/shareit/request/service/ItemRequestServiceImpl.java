package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequestDto addNewRequest(Long userId, NewItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        ItemRequest itemRequest = mapper.mapToItemRequest(itemRequestDto, userId);
        itemRequest = itemRequestRepository.save(itemRequest);
        return mapper.mapToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return itemRequestRepository.findAll().stream().map(mapper::mapToDto).toList();
    }

    @Override
    public ItemRequestDto getById(Long requestId) {
        ItemRequestDto itemRequestDto = mapper.mapToDto(itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(("Запрос с id:" + requestId + " не найден"))));
        itemRequestDto.setItems(itemService.getByRequestId(requestId));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getByRequestorId(Long requestorId) {
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorId(requestorId).stream().map(mapper::mapToDto).toList();
        requests.forEach(request ->
                request.setItems(itemService.getByRequestId(request.getId()))
        );
        return requests;
    }


}
