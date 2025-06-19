package bookingtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItApp.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;


    @Autowired
    ObjectMapper objectMapper;


    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 2, 20, 11, 11))
                .build();

        newBookingRequest = NewBookingRequest.builder()
                .start(LocalDateTime.of(2000, 1, 20, 11, 11))
                .end(LocalDateTime.of(2000, 2, 20, 11, 11))
                .itemId(1L)
                .build();


    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        when(bookingService.addBooking(any(NewBookingRequest.class))).thenReturn(bookingDto);

        String jsonRequest = objectMapper.writeValueAsString(newBookingRequest);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2000-01-20T11:11:00"))
                .andExpect(jsonPath("$.end").value("2000-02-20T11:11:00"));

    }

    @Test
    void respondToBooking_shouldReturnUpdatedBooking() throws Exception {
        when(bookingService.respondToBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2000-01-20T11:11:00"))
                .andExpect(jsonPath("$.end").value("2000-02-20T11:11:00"));

        verify(bookingService, times(1)).respondToBooking(eq(1L), eq(1L), eq(true));
    }

    @Test
    void getAllBookings_shouldReturnListOfBookings() throws Exception {
        when(bookingService.getAllBooking(anyLong(), any())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value("2000-01-20T11:11:00"))
                .andExpect(jsonPath("$[0].end").value("2000-02-20T11:11:00"));

        verify(bookingService, times(1)).getAllBooking(eq(1L), eq(BookingState.ALL));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2000-01-20T11:11:00"))
                .andExpect(jsonPath("$.end").value("2000-02-20T11:11:00"));

        verify(bookingService, times(1)).getBookingById(eq(1L), eq(1L));
    }


}
