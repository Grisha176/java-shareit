package bookingtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingMapper bookingMapper;

    private NewBookingRequest newBookingRequest;
    private BookingDto bookingDto;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {


        newBookingRequest = NewBookingRequest.builder()
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 1, 21, 11, 11))
                .itemId(1L)
                .broker(1L)
                .build();

        booker = User.builder()
                .id(1L)
                .email("example.email.ru")
                .name("userName")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .owner(booker)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 1, 21, 11, 11))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2000, 1, 20, 11, 11))
                .endTime(LocalDateTime.of(2000, 1, 21, 11, 11))
                .item(item)
                .booker(booker)
                .build();

    }

    @Test
    void createBooking_shouldReturnBookingDto_whenDataIsValid() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        when(bookingMapper.mapToBooking(any(NewBookingRequest.class), any(Item.class), any(User.class))).thenReturn(booking);
        when(bookingMapper.mapToDto(any(Booking.class))).thenReturn(bookingDto);


        BookingDto result = bookingService.addBooking(newBookingRequest);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getItem(), result.getItem());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));

    }


    @Test
    void createBooking_shouldReturnBookingDto_whenItemNotFound() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(newBookingRequest));

        verify(bookingRepository, times(0)).save(any(Booking.class));

    }

    @Test
    void createBooking_shouldReturnBookingDto_whenUserNotFound() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(newBookingRequest));


        verify(bookingRepository, times(0)).save(any(Booking.class));

    }

    @Test
    void createBooking_shouldReturnException_whenStartAfterEnd() {

        newBookingRequest.setStart(newBookingRequest.getEnd().plusDays(10));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.addBooking(newBookingRequest));

        assertEquals("Время начала должно быть раньше конца", exception.getMessage());
        verify(bookingRepository, times(0)).save(any(Booking.class));

    }

    @Test
    void createBooking_shouldReturnBookingDto_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        AccessException exception = assertThrows(AccessException.class, () ->
                bookingService.addBooking(newBookingRequest));

        assertEquals("Данная вещь не доступна для брони", exception.getMessage());
        verify(bookingRepository, times(0)).save(any(Booking.class));

    }

    @Test
    void getBooking_shouldReturnBookingDto_whenDataIsValid() {

        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(booker.getId(), bookingDto.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem(), result.getItem());
        verify(bookingRepository, times(1)).findById(any());

    }

    @Test
    void getBooking_shouldReturnBookingDto_whenBookerNotBooker() {
        User user = new User();
        user.setId(99L);
        booking.setBooker(user);
        item.setOwner(user);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(booking));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.getBookingById(1L, bookingDto.getId()));

        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValid() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerId(Mockito.any());
    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValidPast() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndTimeIsBefore(Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerIdAndEndTimeIsBefore(Mockito.any(), Mockito.any());

    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValidFuture() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartTimeIsAfter(Mockito.any(), Mockito.any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerIdAndStartTimeIsAfter(Mockito.any(), Mockito.any());
    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValidCurrent() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Mockito.any(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.CURRENT);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Mockito.any(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class));
    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValidWaiting() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(Mockito.any(), Mockito.any(BookingStatus.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(Mockito.any(), Mockito.any(BookingStatus.class));
    }

    @Test
    void getAllBooking_shouldReturnListBookingDto_whenDataIsValidRejected() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(Mockito.any(), Mockito.any(BookingStatus.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBooking(booker.getId(), BookingState.REJECTED);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(Mockito.any(), Mockito.any(BookingStatus.class));
    }


    @Test
    void respondToBooking_whenOwnerRejectsBooking_thenRejected() throws JsonProcessingException {
        when(bookingRepository.findById(anyLong())).thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToDto(any(Booking.class))).thenReturn(bookingDto);


        BookingDto result = bookingService.respondToBooking(1L, 1L, false);

        verify(bookingRepository).save(booking);
    }

    @Test
    void respondToBooking_whenUserNotFound_thenValidationExceptionThrown() {
        when(userRepository.findById(999L)).thenThrow(new ValidationException("Пользователь не найден"));


        assertThrows(ValidationException.class, () -> bookingService.respondToBooking(999L, 1L, true));
    }

    @Test
    void respondToBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(booker));
        assertThrows(NotFoundException.class, () -> bookingService.respondToBooking(100L, 999L, true));
    }

    @Test
    void respondToBooking_whenUserIsNotOwner_thenAccessExceptionThrown() {
        when(bookingRepository.findById(1L)).thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(200L)).thenReturn(java.util.Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));

        assertThrows(AccessException.class, () -> bookingService.respondToBooking(200L, 1L, true));
    }
}
