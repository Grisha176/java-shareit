package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public BookingDto addBooking(NewBookingRequest request) {
        User broker = userRepository.findById(request.getBroker()).orElseThrow(() -> new NotFoundException("Пользователь с id:" + request.getBroker() + " не найден"));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new NotFoundException("Предмет с id:" + request.getItemId() + " не найден"));
        if (!item.isAvailable()) {
            throw new AccessException("ДАнная вещь не доступна для брони");
        }
        BookingDto bookingDto = BookingMapper.mapToDtoFromNewRequest(request);
        bookingDto.setItem(item);
        bookingDto.setBooker(broker);
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setBroker(broker);
        booking.setItem(item);
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToDto(booking);
    }


    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Запрос на бронирование с id:" + bookingId + " не найден"));
        if (booking.getBroker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.mapToDto(booking);
        }
        throw new ValidationException("Пользователь с id:" + userId + " не является: пользователем вещи или тем кто забронировал вещь");
    }

    @Override
    public List<BookingDto> getAllBooking(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toString()) {
            case "ALL":
                return bookingRepository.findAllByBrokerId(userId).stream().map(BookingMapper::mapToDto).toList();
            case "CURRENT":
                return bookingRepository.findAllByBrokerIdAndStartTimeIsBeforeAndEndTimeIsBefore(userId, now, now).stream().map(BookingMapper::mapToDto).toList();
            case "PAST":
                return bookingRepository.findAllByBrokerIdAndEndTimeIsBefore(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "FUTURE":
                return bookingRepository.findAllByBrokerIdAndStartTimeIsAfter(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "WAITING":
                return bookingRepository.findAllByBrokerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMapper::mapToDto).toList();
            case "REJECTED":
                return bookingRepository.findAllByBrokerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMapper::mapToDto).toList();
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    @Override
    public BookingDto respondToBooking(Long userId, Long bookingId, Boolean status) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь с id:" + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Запрос на бронирование с id:" + bookingId + " не найден"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Предмет с id:" + booking.getItem().getId() + " не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException("Пользователь не являеться владельцем вещи,давать разрешения может только владелец");
        }
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
            item.setAvailable(false);
            return BookingMapper.mapToDto(booking);
        }
        booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllItemBooking(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toString()) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerId(userId).stream().map(BookingMapper::mapToDto).toList();
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore(userId, now, now).stream().map(BookingMapper::mapToDto).toList();
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndTimeIsBefore(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartTimeIsAfter(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMapper::mapToDto).toList();
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMapper::mapToDto).toList();
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

}
