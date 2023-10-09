package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item createItem(long userId, ItemDto itemDto);

    ItemDto findItemById(long userId, long itemId);

    Collection<ItemDto> findAllItemsByUserId(long userId);

    Item updateItem(Long userId, ItemDto itemDto);

    void deleteItemById(long userId, long itemId);

    Collection<Item> getItemsBySearchQuery(String text);

    Comment createComment(long userId, long itemId, CommentDto commentDto);

}