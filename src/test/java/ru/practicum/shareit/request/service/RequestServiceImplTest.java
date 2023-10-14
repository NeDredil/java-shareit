package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("Test@yandex.ru");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request");
        itemRequestDto.setCreated(LocalDateTime.now());

        itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    public void testCreateRequestWhenUserFoundThenRequestCreated() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest result = requestService.createRequest(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequest, result);
    }

    @Test
    public void testCreateRequestWhenValidUserIdAndItemRequestDtoThenRequestCreated() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest createdRequest = requestService.createRequest(userId, itemRequestDto);

        assertNotNull(createdRequest);
        assertEquals(itemRequest.getDescription(), createdRequest.getDescription());
        assertEquals(itemRequest.getRequestor(), createdRequest.getRequestor());
        assertNotNull(createdRequest.getCreated());

        verify(userRepository).findById(userId);
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void testCreateRequestWhenUserNotFoundThenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(user.getId(), itemRequestDto));
    }

    @Test
    public void testFindAllRequestWhenUserFoundThenRequestsRetrieved() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdIsNot(eq(user.getId()), any())).thenReturn(Arrays.asList(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(Arrays.asList(new Item()));

        List<ItemRequestDto> result = requestService.findAllRequest(user.getId(), 0, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllRequestWhenUserNotFoundThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findAllRequest(user.getId(), 0, 1));
    }

    @Test
    public void testGetItemRequestByIdWhenRequestFoundThenReturnRequest() {
        Long requestId = 1L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequest result = requestService.getItemRequestById(requestId);

        assertNotNull(result);
        assertEquals(itemRequest, result);
    }


    @Test
    public void testGetItemRequestByIdWhenRequestNotFoundThenThrowNotFoundException() {
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(itemRequest.getId()));
    }

    @Test
    public void testFindRequestsByIdWhenUserExistsThenReturnRequests() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setRequestor(user);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setRequestor(user);

        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setRequest(itemRequest1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setRequest(itemRequest2);

        List<Item> items = Arrays.asList(item1, item2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestRequestorId(userId)).thenReturn(items);

        List<ItemRequestDto> result = requestService.findRequestsById(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).existsById(userId);
        verify(requestRepository).findAllByRequestorId(userId);
        verify(itemRepository).findAllByRequestRequestorId(userId);
    }

    @Test
    public void testFindRequestsByIdWhenUserNotFoundThenThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestsById(userId));

        verify(userRepository).existsById(userId);
        verify(requestRepository, never()).findAllByRequestorId(userId);
        verify(itemRepository, never()).findAllByRequestRequestorId(userId);
    }

    @Test
    public void testFindRequestByIdWhenUserExistsAndRequestExistsThenReturnRequestDto() {
        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setRequestor(user);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setRequest(itemRequest);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setRequest(itemRequest);

        Collection<Item> items = Arrays.asList(item1, item2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(items);

        ItemRequestDto result = requestService.findRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(items.size(), result.getItems().size());

        verify(userRepository).existsById(userId);
        verify(requestRepository).findById(requestId);
        verify(itemRepository).findAllByRequestId(itemRequest.getId());
    }

    @Test
    public void testFindRequestByIdWhenUserNotFoundThenThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(userId, requestId));

        verify(userRepository).existsById(userId);
        verify(requestRepository, never()).findById(requestId);
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }

    @Test
    public void testFindRequestByIdWhenRequestNotFoundThenThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(userId, requestId));

        verify(userRepository).existsById(userId);
        verify(requestRepository).findById(requestId);
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }

    @Test
    public void testToRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setCreated(LocalDateTime.now());

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Test item 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Test item 2");

        Collection<Item> items = Arrays.asList(item1, item2);

        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest, items);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(items.size(), itemRequestDto.getItems().size());
    }
}