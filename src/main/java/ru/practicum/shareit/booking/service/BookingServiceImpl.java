package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
            throw new AccessException("Данная вещь не доступна для брони");
        }
        Booking booking = BookingMapper.mapToBooking(request,item,broker);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        log.info("Добавление бронирования с id: {}",booking.getId());
        return BookingMapper.mapToDto(booking);
    }


    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Запрос на бронирование с id:" + bookingId + " не найден"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            log.info("Получение бронирования пользователя с id:{}",userId);
            return BookingMapper.mapToDto(booking);
        }
        throw new ValidationException("Пользователь с id:" + userId + " не является: пользователем вещи или тем кто забронировал вещь");
    }

    @Override
    public List<BookingDto> getAllBooking(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toString()) {
            case "ALL":
                log.info("Получение всех бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerId(userId).stream().map(BookingMapper::mapToDto).toList();
            case "CURRENT":
                log.info("Получение текущих бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore(userId, now, now).stream().map(BookingMapper::mapToDto).toList();
            case "PAST":
                log.info("Получение прошлых бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerIdAndEndTimeIsBefore(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "FUTURE":
                log.info("Получение будущих бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerIdAndStartTimeIsAfter(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "WAITING":
                log.info("Получение ожидающих бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMapper::mapToDto).toList();
            case "REJECTED":
                log.info("Получение отклоненных бронирований пользовател с id: {}",userId);
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMapper::mapToDto).toList();
            default:
                log.warn("Unknown state: " + state);
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }


    @Override
    public BookingDto respondToBooking(Long userId, Long bookingId, Boolean status) throws JsonProcessingException {
        userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь с id:" + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Запрос на бронирование с id:" + bookingId + " не найден"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Предмет с id:" + booking.getItem().getId() + " не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException("Пользователь не являеться владельцем вещи,давать разрешения может только владелец");
        }
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);
            log.info("Разрешения бронирования с id: {},владельца с id: {}",booking.getId(),userId);
            item.setAvailable(false);
            return BookingMapper.mapToDto(booking);
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        log.info("Отклонения бронирования с id: {},владельца с id: {}",booking.getId(),userId);
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllItemBooking(Long userId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toString()) {
            case "ALL":
                log.info("Получение всех бронирований вещей пользовател с id: {}",userId);
                return bookingRepository.findAllByItemOwnerId(userId).stream().map(BookingMapper::mapToDto).toList();
            case "CURRENT":
                log.info("Получение текущих бронирований вещи с id: {}",userId);
                return bookingRepository.findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore(userId, now, now).stream().map(BookingMapper::mapToDto).toList();
            case "PAST":
                log.info("Получение прошлых бронирований вещей пользовател с id: {}",userId);
                return bookingRepository.findAllByItemOwnerIdAndEndTimeIsBefore(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "FUTURE":
                log.info("Получение будущих бронирований вещей пользовател с id: {}",userId);
                return bookingRepository.findAllByItemOwnerIdAndStartTimeIsAfter(userId, now).stream().map(BookingMapper::mapToDto).toList();
            case "WAITING":
                log.info("Получение ожидающих бронирований вещей пользовател с id: {}",userId);
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMapper::mapToDto).toList();
            case "REJECTED":
                log.info("Получение отклоненных бронирований вещей пользовател с id: {}",userId);
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMapper::mapToDto).toList();
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

}
