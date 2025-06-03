package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mappers.BookingMapper;
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
import ru.practicum.shareit.item.mappers.ItemMapper;
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


    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        itemDto.setOwnerId(userId);
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(user);
        item = itemRepository.save(item);
        log.info("Создание вещи с id: {}", item.getId());
        return mapToDto(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updatedItem) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        item = ItemMapper.updateItemFields(item, updatedItem);
        item = itemRepository.save(item);
        log.info("Обновление вещи с id: {}", itemId);
        return mapToDto(item);
    }

    @Transactional
    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemRepository.findItemByOwnerId(userId).stream().map(this::mapToDto).toList();
    }

    @Transactional
    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setComments(commentRepository.findAllByItem(item).stream().map(CommentMapper::mapToDto).toList());
        if (item.getOwner().getId().equals(userId)) {
            itemDto = mapToDto(item);
        }
        return itemDto;
    }

    @Transactional
    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow(() -> new NotFoundException("Сущность с id:" + " не найдена"));

        if (bookingRepository.findAllByUserBookings(userId, commentDto.getItemId(), LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("У пользователя с id " + userId + " должно быть хотя бы одно завершенное бронирование предмета с id " + commentDto.getItemId() + " для возможности оставить комментарий.");
        }
        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentDto = CommentMapper.mapToDto(comment);
        commentDto.setItemId(comment.getItem().getId());
        log.info("Добавление комментария с id: {}", commentDto.getId());
        return commentDto;
    }

    private ItemDto mapToDto(Item item) {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setComments(commentRepository.findAllByItem(item).stream().map(CommentMapper::mapToDto).toList());
        Optional<Booking> lastBooking = bookingRepository.findLastFinishedBookingByItem(item);
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItem(item);

        lastBooking.ifPresent(b -> itemDto.setLastBooking(BookingMapper.mapToDto(b)));
        nextBooking.ifPresent(b -> itemDto.setNextBooking(BookingMapper.mapToDto(b)));

        return itemDto;
    }

}
