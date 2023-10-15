package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final long userId = 1L;
    private final long bookingId = 1L;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setItemId(1L);
    }

    @SneakyThrows
    @Test
    void testCreateWhenInvokeThenReturnOk() {
        when(bookingService.createBooking(userId, bookingDto)).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).createBooking(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void testCreateWhenDtoNotValidThenReturnBadRequest() {
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void testReadWhenInvokeThenReturnOk() {
        when(bookingService.findBooking(userId, bookingId)).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).findBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void testReadAllWhenInvokeThenReturnOk() {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.findAllBookingsForOwner(userId, BookingState.ALL, 0, 10)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
        verify(bookingService).findAllBookingsForOwner(userId, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void testUpdateWhenInvokeThenReturnOk() {
        when(bookingService.updateStatusBooking(userId, bookingId, true)).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).updateStatusBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void testDeleteWhenInvokeThenReturnOk() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).deleteBookingById(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void testReadForOwnerWhenInvokeThenReturnOk() {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.findBookingForAllOwnerItems(userId, BookingState.ALL, 0, 10)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
        verify(bookingService).findBookingForAllOwnerItems(userId, BookingState.ALL, 0, 10);
    }
}