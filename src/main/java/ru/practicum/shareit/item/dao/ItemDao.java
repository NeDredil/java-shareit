package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

    public Item createItem(Item item) {
        items.put(item.getId(), item);
        log.debug("Вещь создана.");
        return item;
    }

    public Item findItemById(long itemId) {
        log.debug("Вещь с id: {} найдена.", itemId);
        return items.get(itemId);
    }

    public Collection<Item> findAllItemsByUserId(long userId) {
        Collection<Item> userItems = items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
        log.debug("Всего вещей найдено: {}.", userItems.size());
        return userItems;
    }

    public Item updateItem(Item item) {
        log.debug("Вещь с id: {} обновлена.", item.getId());
        return item;
    }

    public void deleteItemById(long itemId) {
        items.remove(itemId);
    }

    public List<Item> getItemsBySearchQuery(String text) {
        final String textLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textLowerCase) ||
                        item.getDescription().toLowerCase().contains(textLowerCase))
                .collect(Collectors.toList());
    }

}