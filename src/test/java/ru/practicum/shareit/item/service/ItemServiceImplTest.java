package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("userTest@yandex.ru");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment");
        commentDto.setAuthorName(user.getName());
        commentDto.setCreated(comment.getCreated());

        pageRequest = PageRequest.of(0, 10);
    }

    @Test
    void testCreateItemWhenInputIsValidThenReturnItemDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(user.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testCreateItemWhenUserDoesNotExistThenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
    }

    @Test
    void testCreateItemWhenItemRequestDoesNotExistThenThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        itemDto.setRequestId(1L);

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
    }

    @Test
    void testFindItemByIdWhenInputIsValidThenReturnItemDto() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anySet(), any(BookingStatus.class))).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.findItemById(user.getId(), item.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testFindItemByIdWhenInputIsNullThenThrowIllegalArgumentException() {
        assertThrows(NotFoundException.class, () -> itemService.findItemById(user.getId(), 0));
    }

    @Test
    void testFindItemByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(user.getId(), item.getId()));
    }

    @Test
    void testFindAllItemsByUserIdWhenItemsAreFoundThenReturnListOfItemDto() {
        when(itemRepository.findAllByOwnerId(user.getId(), pageRequest)).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anySet(), any(BookingStatus.class))).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemIdIn(anySet())).thenReturn(Collections.singletonList(comment));

        Collection<ItemDto> result = itemService.findAllItemsByUserId(user.getId(), pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.iterator().next().getId()).isEqualTo(item.getId());
    }

    @Test
    void testUpdateItemWhenInputIsValidThenReturnResult() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(user.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testUpdateItemWhenInputIsNullThenThrowIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> itemService.updateItem(user.getId(), null));
    }

    @Test
    void testUpdateItemWhenUserDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), itemDto));
    }

    @Test
    void testUpdateItemWhenItemDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), itemDto));
    }

    @Test
    void testDeleteItemByIdWhenInputIsValidThenItemIsDeleted() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemService.deleteItemById(user.getId(), item.getId());

        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void testDeleteItemByIdWhenUserDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(user.getId(), item.getId()));
    }

    @Test
    void testDeleteItemByIdWhenItemDoesNotExistThenThrowNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(user.getId(), item.getId()));
    }

    @Test
    void testGetItemsBySearchQueryWhenTextIsEmptyThenReturnEmptyList() {
        String searchText = "";
        PageRequest page = PageRequest.of(0, 10);

        Collection<ItemDto> result = itemService.getItemsBySearchQuery(searchText, page);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetItemsBySearchQueryWhenTextIsNotEmptyThenReturnItems() {
        String searchText = "test";
        PageRequest page = PageRequest.of(0, 10);
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());
        when(itemRepository.getItemsBySearchQuery(searchText, page)).thenReturn(items);

        Collection<ItemDto> result = itemService.getItemsBySearchQuery(searchText, page);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(items.size());
    }

    @Test
    void testCreateCommentWhenCommentIsCreatedThenReturnCommentDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(Collections.singletonList(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(user.getId(), item.getId(), commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(comment.getId());
        assertThat(result.getText()).isEqualTo(comment.getText());
        assertThat(result.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void testFindAllByRequestRequestorIdWhenItemsAreFoundThenReturnListOfItemDto() {
        when(itemRepository.findAllByRequestRequestorId(user.getId())).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.findAllByRequestRequestorId(user.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(item.getId());
    }

    @Test
    void testFindAllByRequestIdWhenItemsAreFoundThenReturnListOfItemDto() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.findAllByRequestId(anyLong());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.iterator().next().getId()).isEqualTo(item.getId());
    }
}