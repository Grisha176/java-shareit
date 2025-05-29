package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
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
        return mapToDto(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updatedItem) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена"));
        item = ItemMapper.updateItemFields(item, updatedItem);
        item = itemRepository.save(item);
        return mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemRepository.findItemByOwnerId(userId).stream().map(this::mapToDto).toList();

    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return mapToDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Сущность с id:" + itemId + " не найдена")));
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

    @Override
    public CommentDto addComment(Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow(() -> new NotFoundException("Сущность с id:" + " не найдена"));
        if (bookingRepository.findByItemAndBroker(item, user) == null) {
            throw new ValidationException("Пользователь не может написать отзыв,так как не арендовывал данную вещь");
        }
        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.existsByItemAndStartTimeIsBeforeAndEndTimeIsAfter(item, now, now)) {
            throw new AccessException("Вещь находиться в аренде,написать комментарий можно будет позже");
        }
        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        commentDto = CommentMapper.mapToDto(comment);
        commentDto.setItemId(comment.getItem().getId());
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
