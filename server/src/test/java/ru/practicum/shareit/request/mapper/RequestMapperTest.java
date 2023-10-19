package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {

    @Test
    public void testToRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void testToRequestDtoWithItems() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setCreated(LocalDateTime.now());

        Collection<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");

        items.add(item1);
        items.add(item2);

        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest, items);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void testToRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }
}