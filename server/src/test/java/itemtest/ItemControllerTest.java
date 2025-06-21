package itemtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;

    private UpdateItemRequest updateItemRequest;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Updated Drill");
        updateItemRequest.setDescription("Improved power drill");

        commentDto = CommentDto.builder()
                .text("Great tool!")
                .authorName("User1")
                .created(LocalDateTime.now())
                .build();
    }

    // --- GET /items ---
    @Test
    void getAllItems_shouldReturnListOfItems_whenUserIdProvided() throws Exception {
        when(itemService.getAllItems(100L)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    // --- GET /items/{itemId} ---
    @Test
    void getItemById_shouldReturnItem_whenValidIdAndUser() throws Exception {
        when(itemService.getItemById(100L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    // --- GET /items/search ---
    @Test
    void search_shouldReturnMatchingItems_whenTextProvided() throws Exception {
        when(itemService.search("drill")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    // --- POST /items ---
    @Test
    void createItem_shouldCallServiceAndReturnCreatedItem() throws Exception {
        when(itemService.createItem(100L, itemDto)).thenReturn(itemDto);

        String jsonRequest = mapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    // --- POST /items/{itemId}/comment ---
    @Test
    void addComment_shouldCallServiceAndReturnComment() throws Exception {
        when(itemService.addComment(100L, commentDto)).thenReturn(commentDto);

        String jsonRequest = mapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    // --- PATCH /items/{itemId} ---
    @Test
    void updateItem_shouldCallServiceAndReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(1L, 100L, updateItemRequest)).thenReturn(itemDto);

        String jsonRequest = mapper.writeValueAsString(updateItemRequest);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }


    // --- DELETE /items/{id} ---
    @Test
    void deleteItem_shouldCallServiceAndReturnOk() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{ \"Удаление прошло успешно!\" }"));

        verify(itemService).deleteItem(1L);
    }
}
