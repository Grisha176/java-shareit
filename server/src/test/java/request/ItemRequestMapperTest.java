package request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.mappers.ItemRequestMapper;
import ru.practicum.shareit.mappers.ItemRequestMapperImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
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
}
