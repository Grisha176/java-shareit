package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.RespondItemRequest;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceInMemoryImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;


    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        itemDto.setOwnerId(userId);
        Item item = mapper.mapToItem(itemDto);
        item.setOwner(user);
        item = itemRepository.save(item);
        log.info("Создание вещи с id: {}", item.getId());
        return mapToDto(item);
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updatedItem) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        item = updateItemFields(item, updatedItem);
        item = itemRepository.save(item);
        log.info("Обновление вещи с id: {}", itemId);
        return mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemRepository.findItemByOwnerId(userId).stream().map(this::mapToDto).toList();
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        ItemDto itemDto = mapper.mapToItemDto(item);
        itemDto.setComments(commentRepository.findAllByItem(item).stream().map(commentMapper::mapToDto).toList());
        if (item.getOwner().getId().equals(userId)) {
            itemDto = mapToDto(item);
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow(() -> new NotFoundException("Сущность с id:" + " не найдена"));

        if (bookingRepository.findAllByUserBookings(userId, commentDto.getItemId(), LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("У пользователя с id " + userId + " должно быть хотя бы одно завершенное бронирование предмета с id " + commentDto.getItemId() + " для возможности оставить комментарий.");
        }
        Comment comment = commentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentDto = commentMapper.mapToDto(comment);
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        log.info("Добавление комментария с id: {}", commentDto.getId());
        return commentDto;
    }

    @Override
    public List<RespondItemRequest> getByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream().map(mapper::mapToRespond).toList();
    }

    private ItemDto mapToDto(Item item) {
        ItemDto itemDto = mapper.mapToItemDto(item);
        itemDto.setComments(commentRepository.findAllByItem(item).stream().map(commentMapper::mapToDto).toList());
        Optional<Booking> lastBooking = bookingRepository.findLastFinishedBookingByItem(item);
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItem(item);

        lastBooking.ifPresent(b -> itemDto.setLastBooking(bookingMapper.mapToDto(b)));
        nextBooking.ifPresent(b -> itemDto.setNextBooking(bookingMapper.mapToDto(b)));

        return itemDto;
    }

    private static Item updateItemFields(Item item, UpdateItemRequest updateItemRequest) {

        if (updateItemRequest.hasName()) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.hasDescription()) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.hasAvailable()) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        return item;
    }

}
