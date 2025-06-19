package itemtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceInMemoryImpl;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceInMemoryImpl itemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "John", "john@example.com");
        item = new Item(1L, "Drill", "A powerful drill", true, owner, null);
        itemDto = new ItemDto(1L, "Drill", "A powerful drill", true, 1L, null, null, null, null);

        comment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());
        commentDto = new CommentDto(1L, "Great item!", 1L, "John", LocalDateTime.now());

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2020, 12, 1, 12, 20))
                .end(LocalDateTime.of(2020, 12, 2, 12, 20))
                .status(BookingStatus.WAITING)
                .item(new Item(1L, "Drill", "Powerful drill", true, null, 2L))
                .build();
    }

    // --- createItem ---
    @Test
    void shouldCreateItem_whenValidUserAndData() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(owner.getId(), itemDto);

        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_createItem() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(owner.getId(), itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    // --- deleteItem ---
    @Test
    void shouldDeleteItem_whenExists() {
        doNothing().when(itemRepository).deleteById(item.getId());

        itemService.deleteItem(item.getId());

        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    // --- updateItem ---

    @Test
    void shouldThrowNotFoundException_whenItemNotFound_updateItem() {
        UpdateItemRequest update = new UpdateItemRequest("name", "desc", true, 1L);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(item.getId(), owner.getId(), update))
                .isInstanceOf(NotFoundException.class);
    }

    // --- getAllItems ---
    @Test
    void shouldReturnAllItemsForOwner() {
        when(itemRepository.findItemByOwnerId(owner.getId())).thenReturn(Collections.singletonList(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        Collection<ItemDto> result = itemService.getAllItems(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next()).isEqualTo(itemDto);
    }

    // --- getItemById ---
    @Test
    void shouldGetItemWithCommentsAndBookings() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        when(commentRepository.findAllByItem(item)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.mapToDto(comment)).thenReturn(commentDto);
        when(bookingMapper.mapToDto(any())).thenReturn(bookingDto);
        // Мокаем методы, которые используются внутри getItemById, но не должны возвращать данные
        when(bookingRepository.findLastFinishedBookingByItem(any(Item.class))).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(any(Item.class))).thenReturn(Optional.empty());
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        when(bookingRepository.findLastFinishedBookingByItem(item)).thenReturn(Optional.of(mock(Booking.class)));
        when(bookingRepository.findNextBookingByItem(item)).thenReturn(Optional.of(mock(Booking.class)));

        ItemDto result = itemService.getItemById(owner.getId(), item.getId());

        assertThat(result.getComments()).containsExactly(commentDto);
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
    }

    @Test
    void shouldGetItemWithoutBookingsIfNotOwner() {
        User anotherUser = new User(2L, "Alice", "alice@example.com");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));

        when(commentRepository.findAllByItem(item)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.mapToDto(comment)).thenReturn(commentDto);
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(anotherUser.getId(), item.getId());

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    // --- search ---
    @Test
    void shouldSearchItemsByNameAndDescription() {
        String text = "drill";
        when(itemRepository.findByNameContainingIgnoreCaseAndAvailableTrueOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        Collection<ItemDto> result = itemService.search(text);

        assertThat(result).containsExactly(itemDto);
    }

    @Test
    void shouldReturnEmptyList_whenSearchTextBlank() {
        Collection<ItemDto> result = itemService.search("   ");
        assertThat(result).isEmpty();
    }

    // --- addComment ---
    @Test
    void shouldAddComment_whenUserHasPastBooking() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mock(Booking.class)));

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.mapToDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(owner.getId(), commentDto);

        assertThat(result).isEqualTo(commentDto);
    }

    @Test
    void shouldThrowValidationException_whenNoPastBookings_addComment() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.addComment(owner.getId(), commentDto))
                .isInstanceOf(ValidationException.class);
    }

    // --- getByRequestId ---
    @Test
    void shouldReturnItemsByRequestId() {
        Long requestId = 100L;
        List<Item> items = Collections.singletonList(item);
        List<RespondItemRequest> responses = Collections.singletonList(new RespondItemRequest());

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);
        when(itemMapper.mapToRespond(item)).thenReturn(responses.get(0));

        List<RespondItemRequest> result = itemService.getByRequestId(requestId);

        assertThat(result).isEqualTo(responses);
    }
}