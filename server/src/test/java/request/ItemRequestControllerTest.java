package request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final LocalDateTime now = LocalDateTime.now();

    // --- POST /requests ---
    @Test
    void shouldCreateItemRequest_whenValidInput() throws Exception {
        NewItemRequestDto newRequestDto = new NewItemRequestDto("Хочу дрель");
        ItemRequestDto savedRequestDto = new ItemRequestDto(1L, "Хочу дрель", 1L, List.of(), now.toString());

        when(itemRequestService.addNewRequest(eq(1L), any())).thenReturn(savedRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"description\": \"Хочу дрель\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Хочу дрель")));

        verify(itemRequestService, times(1)).addNewRequest(eq(1L), any());
    }

    // --- GET /requests/{requestId} ---
    @Test
    void shouldGetRequestById() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Хочу дрель", 1L, List.of(), now.toString());


        when(itemRequestService.getById(1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Хочу дрель")));

        verify(itemRequestService, times(1)).getById(1L);
    }

    // --- GET /requests/all ---
    @Test
    void shouldReturnAllRequests() throws Exception {
        ItemRequestDto request1 = new ItemRequestDto(1L, "Хочу дрель", 1L, List.of(), now.toString());
        ItemRequestDto request2 = new ItemRequestDto(2L, "Нужен молоток", 1L, List.of(), now.toString());

        when(itemRequestService.getAllRequests()).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Хочу дрель")))
                .andExpect(jsonPath("$[1].description", is("Нужен молоток")));

        verify(itemRequestService, times(1)).getAllRequests();
    }

    // --- GET /requests ---
    @Test
    void shouldReturnUserRequests() throws Exception {
        ItemRequestDto request1 = new ItemRequestDto(1L, "Хочу дрель", 1L, List.of(), now.toString());
        ItemRequestDto request2 = new ItemRequestDto(2L, "Нужен молоток", 1L, List.of(), now.toString());


        when(itemRequestService.getByRequestorId(1L)).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Хочу дрель")))
                .andExpect(jsonPath("$[1].description", is("Нужен молоток")));

        verify(itemRequestService, times(1)).getByRequestorId(1L);
    }

    // --- Валидация заголовка X-Sharer-User-Id ---
    @Test
    void shouldReturnBadRequest_whenUserIdNotProvided_create() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"description\": \"Хочу дрель\" }"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequest_whenUserIdNotProvided_getAll() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isInternalServerError());
    }

}