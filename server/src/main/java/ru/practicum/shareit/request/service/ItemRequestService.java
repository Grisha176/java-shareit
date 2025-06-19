package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewRequest(Long userId, NewItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getById(Long requestId);

    List<ItemRequestDto> getByRequestorId(Long requestorId);

}
