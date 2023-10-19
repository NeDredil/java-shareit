package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.LittleItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    public void testToItemDtoWhenValidItemThenReturnValidItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    public void testToItemWhenValidItemDtoThenReturnValidItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        Item item = ItemMapper.toItem(itemDto);

        assertNotNull(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    public void testToShortItemWhenValidItemThenReturnValidLittleItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");

        LittleItemDto littleItemDto = ItemMapper.toShortItem(item);

        assertNotNull(littleItemDto);
        assertEquals(item.getId(), littleItemDto.getId());
        assertEquals(item.getName(), littleItemDto.getName());
    }

    @Test
    public void testToFullItemDtoWhenValidItemBookingCommentThenReturnValidItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        user.setName("User Name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment Text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        ItemDto itemDto = ItemMapper.toFullItemDto(item, Arrays.asList(booking), Arrays.asList(comment));

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertFalse(itemDto.getComments().isEmpty());
    }

    @Test
    public void testToItemCollectionDtoWhenValidItemsThenReturnValidItemDtos() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item Name 1");
        item1.setDescription("Item Description 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item Name 2");
        item2.setDescription("Item Description 2");
        item2.setAvailable(false);

        List<Item> items = Arrays.asList(item1, item2);

        List<ItemDto> itemDtos = (List<ItemDto>) ItemMapper.toItemDto(items);

        assertNotNull(itemDtos);
        assertEquals(2, itemDtos.size());
    }

    @Test
    public void testToItemCollectionDtoWhenEmptyItemsThenReturnEmptyItemDtos() {
        List<Item> items = Collections.emptyList();

        List<ItemDto> itemDtos = (List<ItemDto>) ItemMapper.toItemDto(items);

        assertNotNull(itemDtos);
        assertTrue(itemDtos.isEmpty());
    }

    @Test
    public void testToItemCollectionWhenValidItemDtosThenReturnValidItems() {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item Name 1");
        itemDto1.setDescription("Item Description 1");
        itemDto1.setAvailable(true);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item Name 2");
        itemDto2.setDescription("Item Description 2");
        itemDto2.setAvailable(false);

        List<ItemDto> itemDtos = Arrays.asList(itemDto1, itemDto2);

        List<Item> items = (List<Item>) ItemMapper.toItem(itemDtos);

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void testToItemCollectionWhenEmptyItemDtosThenReturnEmptyItems() {
        List<ItemDto> itemDtos = Collections.emptyList();

        List<Item> items = (List<Item>) ItemMapper.toItem(itemDtos);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}