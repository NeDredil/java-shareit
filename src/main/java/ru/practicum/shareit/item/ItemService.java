package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item createItem(long userId, ItemDto itemDto);

    Item findItemById(long userId, long itemId);

    Collection<Item> findAllItemsByUserId(long userId);

    Item updateItem(Long userId, long itemId, ItemDto itemDto);

    void deleteItemById(long userId, long itemId);

    List<Item> getItemsBySearchQuery(String text);

}