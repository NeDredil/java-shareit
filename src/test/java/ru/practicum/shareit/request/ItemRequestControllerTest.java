package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateRequestWhenValidInputThenReturnItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        when(requestService.createRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(requestService, times(1)).createRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    public void testFindRequestsByIdWhenValidInputThenReturnListOfItemRequestDto() throws Exception {
        when(requestService.findRequestsByUserId(anyLong())).thenReturn(Collections.singletonList(new ItemRequestDto()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestService, times(1)).findRequestsByUserId(anyLong());
    }

    @Test
    public void testFindRequestByIdWhenValidInputThenReturnItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        when(requestService.findRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(requestService, times(1)).findRequestById(anyLong(), anyLong());
    }

    @Test
    public void testFindAllRequestWhenValidInputThenReturnListOfItemRequestDto() throws Exception {
        when(requestService.findAllRequest(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(new ItemRequestDto()));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestService, times(1)).findAllRequest(anyLong(), anyInt(), anyInt());
    }

}