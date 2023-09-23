package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    public Item create(long userId, ItemDto itemDto) {
        userDao.isExist(userId);
        return itemDao.create(userId, itemDto);
    }

    public Item read(long userId, long itemId) {
        return itemDao.read(itemId);
    }

    public Collection<Item> readAll(long userId) {
        return itemDao.readAll(userId);
    }

    public Item update(long userId, long itemId, ItemDto itemDto) {
        userDao.isExist(userId);
        return itemDao.update(userId, itemId, itemDto);
    }

    public void delete(long userId, long itemId) {
        itemDao.delete(userId, itemId);
    }

    public List<Item> search(long userId, String text) {
        return itemDao.search(userId, text);
    }

}