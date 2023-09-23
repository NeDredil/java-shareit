package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0;

    public Item create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(getId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        log.debug("Вещь создана.");
        return item;
    }

    public Item read(long itemId) {
        log.debug("Вещь с id: {} найдена.", itemId);
        return items.get(itemId);
    }

    public Collection<Item> readAll(long userId) {
        Collection<Item> userItems = items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
        log.debug("Всего вещей найдено: {}.", userItems.size());
        return userItems;
    }

    public Item update(long userId, long itemId, ItemDto itemDto) {
        isExist(itemId);
        if (!isOwner(userId, itemId)) {
            log.warn("Пользователь с id: {} не является владельцем вещи.", userId);
            throw new NotOwnerException("The user is not the owner of the item");
        }
        Item updatedItem = items.get(itemId);
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        log.debug("Вещь с id: {} обновлена.", itemId);
        return updatedItem;
    }

    public void delete(long userId, long itemId) {
        isExist(itemId);
        if (isOwner(userId, itemId)) {
            items.remove(itemId);
        }
    }

    public List<Item> search(long userId, String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        final String textLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textLowerCase) ||
                        item.getDescription().toLowerCase().contains(textLowerCase))
                .collect(Collectors.toList());
    }

    private long getId() {
        return ++lastId;
    }

    private void isExist(long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn("Вещь с id: {} не найдена.", itemId);
            throw new NotFoundException("Item not found");
        }
    }

    private boolean isOwner(long userId, long itemId) {
        return userId == items.get(itemId).getOwner();
    }

}