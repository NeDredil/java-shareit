package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UncompletedBookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_ITEM;
import static ru.practicum.shareit.exception.Constant.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Item createItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId))));

        Item savedItem = itemRepository.save(item);
        log.debug("Вещь с id: {} добавлена.", savedItem.getId());
        return savedItem;
    }

    public ItemDto findItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, itemId)));

        Collection<Booking> itemBookings;
        if (item.getOwner().getId() == userId) {
            itemBookings = new ArrayList<>(bookingRepository.findAllByItemIdInAndStatus(Set.of(item.getId()), BookingStatus.APPROVED));
        } else {
            itemBookings = Collections.emptyList();
        }
        Collection<Comment> itemComments = commentRepository.findAllByItemId(itemId);
        ItemDto itemDto = ItemMapper.toFullItemDto(item, itemBookings, itemComments);
        log.debug("Вещь с id: {} найдена.", itemId);
        return itemDto;
    }

    public Collection<ItemDto> findAllItemsByUserId(long userId) {
        Map<Long, Item> itemsByOwner = itemRepository.findAllByOwnerId(userId)
                .stream().collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Booking>> bookingsByItems = bookingRepository.findAllByItemIdInAndStatus(itemsByOwner.keySet(),BookingStatus.APPROVED)
                .stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsByItems = commentRepository.findAllByItemIdIn(itemsByOwner.keySet())
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemDto> collect = itemsByOwner.values().stream()
                .map(item -> ItemMapper.toFullItemDto(item,
                        bookingsByItems.getOrDefault(item.getId(), Collections.emptyList()),
                        commentsByItems.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        log.debug("Всего вещей: {} пользователя с id: {}.", collect.size(), userId);
        return collect;
    }

    public Item updateItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        Item updatedItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, item.getId())));
        if (updatedItem.getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не является владельцем вещи.");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        Item saved = itemRepository.save(updatedItem);
        log.debug("Вещь с id: {} обновлена.", item.getId());
        return saved;

    }

    public void deleteItemById(long userId, long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, itemId)));
        if (item.getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не является владельцем вещи.");
        }
        itemRepository.deleteById(itemId);
        log.debug("Вещь с id: {} удалена.", itemId);
    }

    public Collection<Item> getItemsBySearchQuery(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        Collection<Item> searched = itemRepository.getItemsBySearchQuery(text);
        log.debug("Вещей найден: {}.", searched.size());
        return searched;
    }

    public Comment createComment(long userId, long itemId, CommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();

        LocalDateTime time = LocalDateTime.now();
        if (bookingRepository
                .findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(userId, itemId, time, BookingStatus.APPROVED)
                .isEmpty()) {
            throw new UncompletedBookingException("Нельзя создать отзыв для незавершенного бронирования.");
        }
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(time);
        Comment savedComment = commentRepository.save(comment);
        log.debug("Комментарий с id: {} сохранен.", savedComment.getId());
        return comment;
    }
}