package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private long lastId = 0;

    private final ItemDao itemDao;
    private final UserDao userDao;

    public Item createItem(long userId, ItemDto itemDto) {
        userDao.isExist(userId);
        itemDto.setOwner(userId);
        itemDto.setId(getId());
        return itemDao.createItem(ItemMapper.toItem(itemDto));
    }

    public Item findItemById(long userId, long itemId) {
        return itemDao.findItemById(itemId);
    }

    public Collection<Item> findAllItemsByUserId(long userId) {
        return itemDao.findAllItemsByUserId(userId);
    }

    public Item updateItem(Long userId, long itemId, ItemDto itemDto) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item item = itemDao.findItemById(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new NotOwnerException("The user is not the owner of the item");
        }
        return itemDao.updateItem(ItemMapper.toItem(itemDto));
    }

    public void deleteItemById(long userId, long itemId) {
        Item item = itemDao.findItemById(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new NotOwnerException("The user is not the owner of the item");
        }
        itemDao.deleteItemById(itemId);
    }

    public List<Item> getItemsBySearchQuery(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemDao.getItemsBySearchQuery(text);
    }

    private long getId() {
        return ++lastId;
    }
}