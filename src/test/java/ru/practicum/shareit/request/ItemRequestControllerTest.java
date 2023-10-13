package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestDtoList;

    @BeforeEach
    public void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDtoList = Arrays.asList(itemRequestDto);
    }

    @Test
    public void testCreateRequestWhenCalledWithInvalidUserIdThenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFindRequestsByIdWhenCalledWithValidUserIdThenReturnCollectionOfItemRequestDto() throws Exception {
        when(requestService.findRequestsById(anyLong())).thenReturn(itemRequestDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}]"));
    }

    @Test
    public void testFindRequestByIdWhenCalledWithValidUserIdAndItemIdThenReturnItemRequestDto() throws Exception {
        when(requestService.findRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
    }

    @Test
    public void testFindAllRequestWhenCalledWithValidUserIdFromAndSizeThenReturnCollectionOfItemRequestDto() throws Exception {
        when(requestService.findAllRequest(anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemRequestDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}]"));
    }
}