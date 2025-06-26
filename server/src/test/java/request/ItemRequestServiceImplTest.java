package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemService itemService;

    private User user;
    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John", "john@example.com");
        newItemRequestDto = new NewItemRequestDto("Хочу дрель");
        itemRequest = new ItemRequest(1L, "Хочу дрель", user.getId(),LocalDateTime.now(),List.of());
        itemRequestDto = new ItemRequestDto(1L, "Хочу дрель", user.getId(), List.of(), LocalDateTime.now().toString());
    }

    // --- addNewRequest ---
    @Test
    void shouldCreateItemRequest_whenValidUserAndData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.mapToItemRequest(newItemRequestDto, 1L)).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.addNewRequest(1L, newItemRequestDto);

        assertThat(result).isEqualTo(itemRequestDto);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void shouldThrowNotFoundException_whenUserNotFound_addNewRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.addNewRequest(1L, newItemRequestDto))
                .isInstanceOf(NotFoundException.class);
    }

    // --- getAllRequests ---
    @Test
    void shouldReturnAllRequests_whenNotEmpty() {
        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = itemRequestService.getAllRequests();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemRequestDto);
    }

    @Test
    void shouldReturnEmptyList_whenNoRequestsExist() {
        when(itemRequestRepository.findAll()).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests();

        assertThat(result).isEmpty();
    }

    // --- getById ---
    @Test
    void shouldGetRequestById_withItems() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemService.getByRequestId(1L)).thenReturn(itemRequestDto.getItems());

        ItemRequestDto result = itemRequestService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Хочу дрель");
        assertThat(result.getItems()).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_whenRequestNotFound_getById() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    // --- getByRequestorId ---
    @Test
    void shouldGetRequestsByRequestorId_withItems() {
        when(itemRequestRepository.findAllByRequestorId(1L)).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemService.getByRequestId(1L)).thenReturn(itemRequestDto.getItems());

        List<ItemRequestDto> result = itemRequestService.getByRequestorId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).isNotNull();
    }

    @Test
    void shouldReturnEmptyList_whenNoRequestsForRequestor() {
        when(itemRequestRepository.findAllByRequestorId(999L)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getByRequestorId(999L);

        assertThat(result).isEmpty();
    }
}