package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    public static final Long ID = 1L;
    public static final String FROM_VALUE = "0";
    public static final String SIZE_VALUE = "20";
    public static final String TEXT_VALUE = "text";
    public static final String FROM_PARAM = "from";
    public static final String SIZE_PARAM = "size";
    public static final String TEXT_PARAM = "text";
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    @MockBean
    ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void createItemTest() throws Exception {
        ItemDto inputDto = generateItemInputDto();
        ItemDto responseDto = generateItemResponseDto(inputDto);

        when(itemService.createItem(any(Long.class), any(ItemDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription()), String.class));

        verify(itemService, times(1))
                .createItem(any(Long.class), any(ItemDto.class));
    }

    @Test
    public void createCommentTest() throws Exception {
        CommentDto inputCommentDto = new CommentDto();
        inputCommentDto.setText("text");

        when(itemService.createComment(any(Long.class), any(Long.class), any(CommentDto.class)))
                .thenReturn(inputCommentDto);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .header(USER_ID_HEADER, ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(inputCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(inputCommentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(inputCommentDto.getText()), String.class));

        verify(itemService, times(1))
                .createComment(any(Long.class), any(Long.class), any(CommentDto.class));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto inputDto = generateItemInputDto();
        inputDto.setName("updatedName");
        ItemDto responseDto = generateItemResponseDto(inputDto);

        when(itemService.updateItem(any(Long.class), any(ItemDto.class)))
                .thenReturn(responseDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseDto.getName()), String.class));

        verify(itemService, times(1))
                .updateItem(any(Long.class), any(ItemDto.class));
    }

    @Test
    public void findItemByIdTest() throws Exception {
        ItemDto responseDto = generateItemResponseDto(generateItemInputDto());

        when(itemService.findItemById(any(Long.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));

        verify(itemService, times(1)).findItemById(any(Long.class), any(Long.class));
    }

    @Test
    public void findAllItemsTest() throws Exception {
        when(itemService.findAllItemsByUserId(any(Integer.class), any(PageRequest.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, ID)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .findAllItemsByUserId(any(Long.class), any(PageRequest.class));
    }

    @Test
    public void testDeleteItemByIdWhenItemExistsThenReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItemById(ID, ID);

        mvc.perform(delete("/items/" + ID)
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItemById(ID, ID);
    }

    @Test
    public void testDeleteItemByIdWhenItemDoesNotExistThenReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Item not found")).when(itemService).deleteItemById(ID, ID);

        mvc.perform(delete("/items/" + ID)
                        .header(USER_ID_HEADER, ID))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).deleteItemById(ID, ID);
    }

    @Test
    public void testDeleteItemByIdWhenUserIdHeaderNotProvidedThenReturnBadRequest() throws Exception {
        mvc.perform(delete("/items/" + ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetItemsBySearchQueryWhenServiceReturnsEmptyListThenReturnEmptyJsonArray() throws Exception {
        when(itemService.getItemsBySearchQuery(anyString(), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search")
                        .param(TEXT_PARAM, TEXT_VALUE)
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .getItemsBySearchQuery(anyString(), any(PageRequest.class));
    }

    @Test
    public void testGetItemsBySearchQueryWhenTextParamNotProvidedThenReturnBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .param(FROM_PARAM, FROM_VALUE)
                        .param(SIZE_PARAM, SIZE_VALUE))
                .andExpect(status().isBadRequest());
    }

    private CommentDto generateResponseCommentDto(Long id, CommentDto dto) {
        CommentDto result = new CommentDto();
        result.setId(id);
        result.setAuthorName("name");
        result.setText(dto.getText());
        result.setCreated(CREATION_DATE);
        return result;
    }

    private ItemDto generateItemInputDto() {
        ItemDto dto = new ItemDto();
        dto.setName("name");
        dto.setDescription("description");
        dto.setAvailable(true);
        return dto;
    }

    private ItemDto generateItemResponseDto(ItemDto inputDto) {
        ItemDto dto = new ItemDto();
        dto.setId(inputDto.getId());
        dto.setName(inputDto.getName());
        dto.setDescription(inputDto.getDescription());
        dto.setAvailable(inputDto.getAvailable());
        return dto;
    }
}
