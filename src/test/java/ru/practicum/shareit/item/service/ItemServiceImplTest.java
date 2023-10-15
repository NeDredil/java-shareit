package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.LittleItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User user;
    private Item item;
    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(2L)
                .name("ownerTest")
                .email("owerTest@yandex.ru")
                .build();

        user = User.builder()
                .id(3L)
                .name("userTest")
                .email("userTest@yandex.ru")
                .build();

        item = Item.builder()
                .id(4L)
                .name("itemNameTest")
                .description("itemDescTest")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void createItem_withValidData_shouldReturnSavedItem() {
        long userId = 1L;
        long requestId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        Item item = new Item();
        item.setId(1L);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item savedItem = itemService.createItem(userId, itemDto);

        assertNotNull(savedItem);
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());
        assertEquals(user, savedItem.getOwner());
        assertEquals(itemRequest, savedItem.getRequest());
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_withInvalidUserId_shouldThrowNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(user.getId());
        verify(requestRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_withInvalidRequestId_shouldThrowNotFoundException() {
        long requestId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(user.getId());
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testFindItemByIdWhenItemIsFoundThenReturnItemDto() {
        long itemId = 1L;

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anySet(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDto itemDto = itemService.findItemById(user.getId(), itemId);

        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatus(anySet(), any());
        verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    void testFindItemByIdWhenItemIsNotFoundThenThrowNotFoundException() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(user.getId(), item.getId()));
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).findAllByItemIdInAndStatus(anySet(), any());
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void testFindItemByIdWhenUserIsNotOwnerThenReturnItemDtoWithoutBookings() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.findItemById(user.getId(), item.getId());

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).findAllByItemIdInAndStatus(anySet(), any());
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
    }

    @Test
    void testUpdateItemWhenInputIsValidThenReturnResult() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        User user = new User();
        user.setId(user.getId());

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(false);
        item.setOwner(user);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = itemService.updateItem(user.getId(), itemDto);

        assertNotNull(updatedItem);
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItemWhenUserIdDoesNotExistThenThrowNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), itemDto));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testDeleteItemByIdWhenInputIsValidThenDeleteItem() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemService.deleteItemById(owner.getId(), item.getId());

        verify(userRepository, times(1)).existsById(owner.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void testDeleteItemByIdWhenUserIdDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(user.getId(), item.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteItemByIdWhenItemIdDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(user.getId(), item.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteItemByIdWhenUserIsNotOwnerThenThrowNotOwnerException() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotOwnerException.class, () -> itemService.deleteItemById(user.getId(), item.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetItemsBySearchQueryWhenTextIsEmptyThenReturnEmptyList() {
        String text = "";

        Collection<Item> items = itemService.getItemsBySearchQuery(text, 0, 10);

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, never()).getItemsBySearchQuery(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetItemsBySearchQueryWhenTextIsBlankThenReturnEmptyList() {
        String text = "   ";

        Collection<Item> items = itemService.getItemsBySearchQuery(text, 0, 10);

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, never()).getItemsBySearchQuery(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetItemsBySearchQueryWhenTextIsValidThenReturnItems() {
        String text = "search query";

        PageRequest page = PageRequest.of(from / size, size);
        List<Item> itemList = List.of(new Item(), new Item());

        when(itemRepository.getItemsBySearchQuery(text, page)).thenReturn(itemList);

        Collection<Item> items = itemService.getItemsBySearchQuery(text, from, size);

        assertNotNull(items);
        assertEquals(itemList.size(), items.size());
        verify(itemRepository, times(1)).getItemsBySearchQuery(text, page);
    }

    @Test
    void testFindAllItemsByUserIdWhenInputIsValidThenReturnResult() {
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> itemList = List.of(item);

        when(itemRepository.findAllByOwnerId(owner.getId(), page)).thenReturn(itemList);

        Collection<ItemDto> items = itemService.findAllItemsByUserId(owner.getId(), from, size);

        assertNotNull(items);
        assertEquals(itemList.size(), items.size());
        verify(itemRepository, times(1)).findAllByOwnerId(owner.getId(), page);
    }

    @Test
    void testFindAllItemsByUserIdWhenInputIsEmptyThenReturnZero() {
        PageRequest page = PageRequest.of(from / size, size);

        when(itemRepository.findAllByOwnerId(user.getId(), page)).thenReturn(Collections.emptyList());

        Collection<ItemDto> items = itemService.findAllItemsByUserId(user.getId(), from, size);

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, times(1)).findAllByOwnerId(user.getId(), page);
    }

    @Test
    public void testToShortItemWhenItemProvidedThenLittleItemDtoReturnedWithCorrectProperties() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        LittleItemDto littleItemDto = ItemMapper.toShortItem(item);

        assertEquals(item.getId(), littleItemDto.getId());
        assertEquals(item.getName(), littleItemDto.getName());
    }

    @Test
    void testToFullItemDtoWhenValidItemThenReturnItemDtoWithCorrectData() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment1");
        comment.setItem(item);
        comment.setAuthor(user);

        ItemDto itemDto = ItemMapper.toFullItemDto(item, Collections.singletonList(booking), Collections.singletonList(comment));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemDto.getComments());
    }

    @Test
    public void testToShortItemWhenValidItemThenCorrectMapping() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        LittleItemDto littleItemDto = ItemMapper.toShortItem(item);

        assertEquals(item.getId(), littleItemDto.getId());
        assertEquals(item.getName(), littleItemDto.getName());
    }

    @Test
    void testToShortItemWhenInputIsNullThenThrowNullPointerException() {
        Item item = null;

        assertThrows(NullPointerException.class, () -> ItemMapper.toShortItem(item));
    }

    @Test
    void testToShortItemWhenInputIsValidThenReturnResult() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        LittleItemDto littleItemDto = ItemMapper.toShortItem(item);

        assertNotNull(littleItemDto);
        assertEquals(item.getId(), littleItemDto.getId());
        assertEquals(item.getName(), littleItemDto.getName());
    }

    @Test
    public void testFindAllByRequestRequestorId() {
        long userId = 1;
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());

        when(itemRepository.findAllByRequestRequestorId(userId)).thenReturn(items);

        Collection<Item> result = itemService.findAllByRequestRequestorId(userId);

        assertEquals(items, result);
    }

    @Test
    void testFindAllByRequestIdWhenInputIsValidThenReturnResult() {
        long requestId = 1L;
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        List<Item> result = new ArrayList<>(itemService.findAllByRequestId(requestId));

        assertEquals(items, result);
    }
}
