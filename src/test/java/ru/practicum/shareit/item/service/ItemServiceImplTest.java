package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testFindItemByIdWhenValidParametersThenItemDtoReturned() {
        long userId = 1L;
        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        item.getOwner().setId(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        ItemDto result = itemService.findItemById(userId, itemId);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void testFindAllItemsByUserIdWhenValidParametersThenItemDtosReturned() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Item item = new Item();
            item.setId(i + 1L);
            item.setOwner(new User());
            item.getOwner().setId(userId);
            items.add(item);
        }
        when(itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size))).thenReturn(items);

        // Act
        Collection<ItemDto> result = itemService.findAllItemsByUserId(userId, from, size);

        // Assert
        assertNotNull(result);
        assertEquals(size, result.size());
    }
    @Test
    void testDeleteItemByIdWhenValidParametersThenItemDeleted() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User());
        item.getOwner().setId(userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        itemService.deleteItemById(userId, itemId);

        // Assert
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void testGetItemsBySearchQueryWhenValidParametersThenItemsReturned() {
        String text = "test";
        int from = 0;
        int size = 5;
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Item item = new Item();
            item.setId(i + 1L);
            item.setName(text + i);
            items.add(item);
        }
        when(itemRepository.getItemsBySearchQuery(text, PageRequest.of(from / size, size))).thenReturn(items);

        // Act
        Collection<Item> result = itemService.getItemsBySearchQuery(text, from, size);

        // Assert
        assertNotNull(result);
        assertEquals(size, result.size());
    }

    @Test
    void testFindAllByRequestRequestorIdWhenValidParametersThenItemsReturned() {
        long userId = 1L;
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            item.setId(i + 1L);
            items.add(item);
        }
        when(itemRepository.findAllByRequestRequestorId(userId)).thenReturn(items);

        // Act
        Collection<Item> result = itemService.findAllByRequestRequestorId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(items.size(), result.size());
    }

    @Test
    void testFindAllByRequestIdWhenValidParametersThenItemsReturned() {
        long requestId = 1L;
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            item.setId(i + 1L);
            items.add(item);
        }
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        // Act
        Collection<Item> result = itemService.findAllByRequestId(requestId);

        // Assert
        assertNotNull(result);
        assertEquals(items.size(), result.size());
    }
}