package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RequestRepositoryTest {

    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllByRequestorId() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Test")
                .email("test@yandex.ru")
                .build();

        ItemRequest request1 = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        List<ItemRequest> expectedRequests = new ArrayList<>();
        expectedRequests.add(request1);
        expectedRequests.add(request2);

        when(requestRepository.findAllByRequestorId(userId)).thenReturn(expectedRequests);

        Collection<ItemRequest> actualRequests = requestRepository.findAllByRequestorId(userId);

        assertEquals(expectedRequests.size(), actualRequests.size());
        assertEquals(expectedRequests, actualRequests);

        verify(requestRepository, times(1)).findAllByRequestorId(userId);
    }

    @Test
    public void testFindAllByRequestorIdIsNot() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Test")
                .email("test@yandex.ru")
                .build();

        ItemRequest request1 = ItemRequest.builder()
                .id(1L)
                .description("Request 1")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        List<ItemRequest> expectedRequests = new ArrayList<>();
        expectedRequests.add(request1);
        expectedRequests.add(request2);

        Pageable page = Pageable.unpaged();

        when(requestRepository.findAllByRequestorIdIsNot(userId, page)).thenReturn(expectedRequests);

        List<ItemRequest> actualRequests = requestRepository.findAllByRequestorIdIsNot(userId, page);

        assertEquals(expectedRequests.size(), actualRequests.size());
        assertEquals(expectedRequests, actualRequests);

        verify(requestRepository, times(1)).findAllByRequestorIdIsNot(userId, page);
    }
}