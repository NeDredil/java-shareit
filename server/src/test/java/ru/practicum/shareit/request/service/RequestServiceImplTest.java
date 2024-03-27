package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRequestWhenValidUserIdAndDtoThenReturnDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = requestService.createRequest(userId, itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    public void testFindRequestsByUserIdWhenValidUserIdThenReturnListOfDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(Collections.singletonList(itemRequest));

        assertEquals(1, requestService.findRequestsByUserId(userId).size());
    }

    @Test
    public void testFindRequestsByUserIdWhenNonExistentUserIdThenReturnEmptyList() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestsByUserId(userId));
    }

    @Test
    public void testFindRequestsByUserIdWhenValidUserIdThenReturnListOfDtoNoFound() {
        Long userId = 1L;
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(Collections.singletonList(itemRequest));

        assertEquals(1, requestService.findRequestsByUserId(userId).size());
    }

    @Test
    public void testFindRequestByIdWhenValidUserIdAndRequestIdThenReturnDto() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Arrays.asList(new Item()));

        assertEquals(requestId, requestService.findRequestById(userId, requestId).getId());
    }

    @Test
    public void testFindRequestByIdWhenNonExistentUserIdThenThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(userId, requestId));
    }

    @Test
    public void testFindRequestByIdWhenNonExistentRequestIdThenThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(userId, requestId));
    }

    @Test
    public void testFindAllRequestWhenValidUserIdAndFromAndSizeThenReturnListOfDto() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdIsNot(userId, PageRequest.of(from, size, Sort.by("created").ascending()))).thenReturn(Arrays.asList(itemRequest));

        assertEquals(1, requestService.findAllRequest(userId, from, size).size());
    }

    @Test
    public void testFindAllRequestWhenNonExistentUserIdThenThrowNotFoundException() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.findAllRequest(userId, from, size));
    }

    @Test
    public void testFindAllRequestWhenValidUserIdButNoRequestsThenReturnEmptyList() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdIsNot(userId, PageRequest.of(from, size, Sort.by("created").ascending()))).thenReturn(Collections.emptyList());

        assertEquals(0, requestService.findAllRequest(userId, from, size).size());
    }

    @Test
    public void testGetItemRequestByIdWhenValidRequestIdThenReturnDto() {
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setCreated(LocalDateTime.now());

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertEquals(requestId, requestService.getItemRequestById(requestId).getId());
    }

    @Test
    public void testCreateRequestWhenInvalidUserIdThenThrowNotFoundException() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(userId, itemRequestDto));
    }
}