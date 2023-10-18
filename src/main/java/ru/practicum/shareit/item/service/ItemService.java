package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto findItemById(long userId, long itemId);

    Collection<ItemDto> findAllItemsByUserId(long userId, PageRequest page);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    void deleteItemById(long userId, long itemId);

    Collection<ItemDto> getItemsBySearchQuery(String text, PageRequest page);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

    Collection<ItemDto> findAllByRequestRequestorId(long userId);

    Collection<ItemDto> findAllByRequestId(long requestId);

}