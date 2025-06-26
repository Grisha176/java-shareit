package request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.RespondItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.mappers.ItemRequestMapperImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        itemRequestMapper = new ItemRequestMapperImpl();
    }

    @Test
    void mapToItemRequest_WithValidDto_ShouldReturnItemRequest() {
        // Given
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("Need a drill")
                .build();

        Long requestorId = 100L;

        // When
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto, requestorId);

        // Then
        assertNotNull(itemRequest);
        assertEquals("Need a drill", itemRequest.getDescription());
        assertEquals(requestorId, itemRequest.getRequestorId());
        assertNull(itemRequest.getId()); // id не устанавливается через Dto
    }

    @Test
    void mapToItemRequest_WhenDtoIsNull_ShouldSetOnlyRequestorId() {
        // Given
        NewItemRequestDto dto = null;
        Long requestorId = 200L;

        // When
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto, requestorId);

        // Then
        assertNotNull(itemRequest);
        assertNull(itemRequest.getDescription());
        assertEquals(requestorId, itemRequest.getRequestorId());
    }

    @Test
    void mapToItemRequest_WhenBothNull_ShouldReturnNull() {
        // Given
        NewItemRequestDto dto = null;
        Long requestorId = null;

        // When
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto, requestorId);

        // Then
        assertNull(itemRequest);
    }

    @Test
    void mapToDto_WithValidItemRequest_ShouldReturnDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a drill");
        itemRequest.setRequestorId(100L);
        itemRequest.setCreatedTime(now);

        // When
        ItemRequestDto dto = itemRequestMapper.mapToDto(itemRequest);

        // Then
        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequestorId(), dto.getRequestorId());
    }

    @Test
    void mapToDto_WhenItemRequestHasNoData_ShouldHandleGracefully() {
        // Given
        ItemRequest itemRequest = new ItemRequest();

        // When
        ItemRequestDto dto = itemRequestMapper.mapToDto(itemRequest);

        // Then
        assertNotNull(dto);
        assertNull(dto.getDescription());
        assertNull(dto.getRequestorId());
        assertNull(dto.getId());
    }

    @Test
    void testMapToItemRequest_withValidDtoAndUserId_shouldMapCorrectly() {
        // Given
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна вещь");

        Long userId = 1L;

        // When
        ItemRequest result = itemRequestMapper.mapToItemRequest(dto, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна вещь");
        assertThat(result.getRequestorId()).isEqualTo(userId);
        assertNull(result.getItems());
    }

    @Test
    void testMapToItemRequest_withNullDtoAndUserId_shouldReturnNotNull() {
        // When
        ItemRequest result = itemRequestMapper.mapToItemRequest(null, null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testMapToDto_withValidItemRequest_shouldMapCorrectly() {
        // Given
        Item item = Item.builder()
                .id(1L)
                .name("Телефон")
                .description("Хороший смартфон")
                .available(true)
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(100L)
                .description("Нужен телефон")
                .requestorId(1L)
                .createdTime(now)
                .items(List.of(item))
                .build();

        // When
        ItemRequestDto dto = itemRequestMapper.mapToDto(request);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getDescription()).isEqualTo("Нужен телефон");
        assertThat(dto.getRequestorId()).isEqualTo(1L);
        assertThat(dto.getCreated()).isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        List<RespondItemRequest> items = dto.getItems();
        assertFalse(items.isEmpty());
        RespondItemRequest itemDto = items.get(0);
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Телефон");
    }

    @Test
    void testMapToDto_withEmptyItems_shouldMapWithoutItems() {
        // Given
        ItemRequest request = ItemRequest.builder()
                .id(100L)
                .description("Нужен телефон")
                .requestorId(1L)
                .createdTime(now)
                .items(null)
                .build();

        // When
        ItemRequestDto dto = itemRequestMapper.mapToDto(request);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getItems()).isNull();
    }

    @Test
    void testMapToDto_withNullFields_shouldNotFail() {
        // Given
        ItemRequest request = new ItemRequest();

        // When
        ItemRequestDto dto = itemRequestMapper.mapToDto(request);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getItems()).isNull();
    }
}
