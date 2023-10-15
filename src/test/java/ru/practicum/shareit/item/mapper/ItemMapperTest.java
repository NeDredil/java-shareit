package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.LittleItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;
    private List<Booking> bookings;
    private List<Comment> comments;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");
        itemDto.setDescription("Description1");
        itemDto.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        bookings = Arrays.asList(booking);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setId(1L);
        comment.setText("Comment1");
        comments = Arrays.asList(comment);
    }

    @Test
    public void testToItemDtoWhenValidItemThenReturnItemDto() {
        ItemDto result = ItemMapper.toItemDto(item);
        assertThat(result).isEqualToComparingFieldByField(itemDto);
    }

    @Test
    public void testToItemDtoWhenItemWithNullPropertiesThenReturnItemDtoWithNullProperties() {
        item = new Item();
        ItemDto result = ItemMapper.toItemDto(item);
        assertThat(result).isEqualToComparingFieldByField(new ItemDto());
    }

    @Test
    public void testToItemWhenValidItemDtoThenReturnItem() {
        Item result = ItemMapper.toItem(itemDto);
        assertThat(result).isEqualToComparingFieldByField(item);
    }

    @Test
    public void testToShortItemWhenValidItemThenReturnLittleItemDto() {
        LittleItemDto result = ItemMapper.toShortItem(item);
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
    }
}