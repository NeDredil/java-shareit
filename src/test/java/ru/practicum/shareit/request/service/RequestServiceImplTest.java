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
    public void testCreateRequestWhenUserNotFoundThenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(user.getId(), itemRequestDto));
    }

    @Test
    public void testFindRequestsByIdWhenUserNotFoundThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestsById(user.getId()));
    }

    @Test
    public void testFindRequestByIdWhenUserNotFoundThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(user.getId(), itemRequest.getId()));
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
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest result = requestService.getItemRequestById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest, result);
    }

    @Test
    public void testGetItemRequestByIdWhenRequestNotFoundThenThrowNotFoundException() {
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getItemRequestById(itemRequest.getId()));
    }
}