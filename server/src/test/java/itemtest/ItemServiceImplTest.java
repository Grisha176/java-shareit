package itemtest;

import jakarta.persistence.EntityNotFoundException;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private ItemRequestRepository itemRequestRepository;


    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private BookingDto bookingDto;
    private LocalDateTime now;
    private User user;


    @BeforeEach
    void setUp() {

        now = LocalDateTime.now();

        user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        commentDto = new CommentDto();
        commentDto.setText("Хорошая вещь!");
        commentDto.setItemId(100L);
        commentDto.setAuthorName("Alice");

        // Инициализируем сущность Comment

        owner = new User(1L, "John", "john@example.com");
        item = new Item(1L, "Drill", "A powerful drill", true, owner, null);
        itemDto = new ItemDto(1L, "Drill", "A powerful drill", true, 1L, null, null, null, null);

        comment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());
        commentDto = new CommentDto(1L, "Great item!", 1L, "John", LocalDateTime.now());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2020, 12, 1, 12, 20))
                .end(LocalDateTime.of(2020, 12, 2, 12, 20))
                .status(BookingStatus.WAITING)
                .item(new Item(1L, "Drill", "Powerful drill", true, null, itemRequest))
                .build();
    }

    @Test
    void createItem_whenRequestNotFound_shouldThrowException() {
        Long userId = 1L;
        Long requestId = 999L;


        User user = new User();
        user.setId(userId);

        ItemDto itemDto = ItemDto.builder()
                .name("Телефон")
                .description("Хороший смартфон")
                .available(true)
                .requestId(requestId)
                .ownerId(1L)
                .build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        when(itemMapper.mapToItem(any())).thenReturn(item);

        assertThatThrownBy(() -> itemService.createItem(userId, itemDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Запроса на данный предмет не существует");
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

    @Test
    void addComment_WhenUserNotFound_ShouldThrowNotFoundException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When + Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(999L, commentDto));

        assertTrue(exception.getMessage().contains("Пользователь с id:"));
    }

    @Test
    void mapToDto_WhenItemHasComments_ShouldSetComments() {
        // Given
        CommentDto commentDto = CommentDto.builder()
                .id(100L)
                .text("Good tool")
                .build();

        when(commentRepository.findAllByItem(item)).thenReturn(List.of(new Comment()));
        when(commentMapper.mapToDto(any(Comment.class))).thenReturn(commentDto);
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        // When
        ItemDto dto = itemService.mapToDto(item);

        // Then
        assertNotNull(dto.getComments());
        assertThat(dto.getComments()).hasSize(1).contains(commentDto);
    }

    @Test
    void mapToDto_WhenLastBookingExists_ShouldSetLastBooking() {
        // Given
        Booking lastBooking = Booking.builder()
                .id(500L)
                .startTime(now.minusDays(2))
                .endTime(now.minusDays(1))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(new User())
                .build();

        when(bookingRepository.findLastFinishedBookingByItem(item)).thenReturn(Optional.of(lastBooking));
        when(bookingMapper.mapToDto(lastBooking)).thenReturn(BookingDto.builder().id(500L).build());
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        // When
        ItemDto dto = itemService.mapToDto(item);

        // Then
        assertNotNull(dto.getLastBooking());
        assertEquals(500L, dto.getLastBooking().getId());
    }

    @Test
    void mapToDto_WhenNextBookingExists_ShouldSetNextBooking() {
        // Given
        Booking nextBooking = Booking.builder()
                .id(600L)
                .startTime(now.plusDays(1))
                .endTime(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(new User())
                .build();

        when(bookingRepository.findNextBookingByItem(item)).thenReturn(Optional.of(nextBooking));
        when(bookingMapper.mapToDto(nextBooking)).thenReturn(BookingDto.builder().id(600L).build());
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        // When
        ItemDto dto = itemService.mapToDto(item);

        // Then
        assertNotNull(dto.getNextBooking());
        assertEquals(600L, dto.getNextBooking().getId());
    }

    @Test
    void mapToDto_WhenNoBookings_ShouldNotSetLastAndNextBooking() {
        // Given
        when(bookingRepository.findLastFinishedBookingByItem(item)).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(item)).thenReturn(Optional.empty());
        when(itemMapper.mapToItemDto(any())).thenReturn(itemDto);

        // When
        ItemDto dto = itemService.mapToDto(item);

        // Then
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void updateItemFields_WhenNameIsProvided_ShouldSetName() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Updated name");

        // When
        Item updated = itemService.updateItemFields(item, request);

        // Then
        assertEquals("Updated name", updated.getName());
    }

    @Test
    void updateItemFields_WhenDescriptionIsProvided_ShouldSetDescription() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setDescription("New description");

        // When
        Item updated = itemService.updateItemFields(item, request);

        // Then
        assertEquals("New description", updated.getDescription());
    }

    @Test
    void updateItemFields_WhenAvailableIsTrue_ShouldSetAvailable() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setAvailable(true);

        // When
        Item updated = itemService.updateItemFields(item, request);

        // Then
        assertTrue(updated.isAvailable());
    }

    @Test
    void updateItemFields_WhenAvailableIsFalse_ShouldSetAvailable() {
        // Given
        item.setAvailable(true);
        UpdateItemRequest request = new UpdateItemRequest();
        request.setAvailable(false);

        // When
        Item updated = itemService.updateItemFields(item, request);

        // Then
        assertFalse(updated.isAvailable());
    }

    @Test
    void updateItemFields_WhenNoUpdates_ShouldPreserveOriginalValues() {
        // Given

        item.setDescription("Powerful drill");
        UpdateItemRequest request = new UpdateItemRequest(); // все null/blank

        // When
        Item updated = itemService.updateItemFields(item, request);

        // Then
        assertEquals("Drill", updated.getName());
        assertEquals("Powerful drill", updated.getDescription());
        assertTrue(updated.isAvailable());
    }


    @Test
    void updateItem_shouldNotUpdateIfNameIsNull() {
        // Given
        Long itemId = 1L;
        Long userId = 2L;

        UpdateItemRequest request = new UpdateItemRequest();
        request.setDescription("Обновленное описание");
        request.setAvailable(true);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Старое имя");
        existingItem.setDescription("Старое описание");
        existingItem.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.mapToItemDto(existingItem)).thenReturn(ItemDto.builder()
                .id(itemId)
                .name("Старое имя")
                .description("Обновленное описание")
                .available(true)
                .requestId(10L)
                .build());

        // When
        ItemDto result = itemService.updateItem(itemId, userId, request);

        // Then
        assertThat(result.getName()).isEqualTo("Старое имя"); // не изменилось
        assertThat(result.getDescription()).isEqualTo("Обновленное описание");
        assertThat(result.getAvailable()).isTrue(); // изменилось
        assertThat(result.getRequestId()).isEqualTo(10L); // не изменилось
    }

    @Test
    void updateItem_whenUserNotFound_shouldThrowNotFoundException() {
        // Given
        Long itemId = 1L;
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> itemService.updateItem(itemId, userId, new UpdateItemRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id:" + userId + " не найден");
    }

    @Test
    void updateItem_whenItemNotFound_shouldThrowNotFoundException() {
        // Given
        Long itemId = 999L;
        Long userId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());


        // When / Then
        assertThatThrownBy(() -> itemService.updateItem(itemId, userId, new UpdateItemRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id:1 не найден");
    }


}