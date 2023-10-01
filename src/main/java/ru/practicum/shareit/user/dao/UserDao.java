package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserDao {
    private long lastId = 0;
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        if (checkEmail(user)) {
            log.warn("Пользователь с email: {} уже существует.", user.getEmail());
            throw new EmailExistException("Email duplicate error");
        }
        user.setId(getId());
        users.put(user.getId(), user);
        log.debug("Пользователь создан.");
        return user;
    }

    public User read(long userId) {
        log.debug("Пользователь с id: {} найден.", userId);
        return users.get(userId);
    }

    public Collection<User> readAll() {
        log.debug("Всего пользователей: {}.", users.values().size());
        return users.values();
    }

    public User update(long userId, User user) {
        isExist(userId);
        User updatedUser = users.get(userId);
        if (user.getEmail() != null) {
            users.remove(userId);
            if (checkEmail(user)) {
                users.put(updatedUser.getId(), updatedUser);
                log.warn("Пользователь с email: {} уже существует.", user.getEmail());
                throw new EmailExistException("Email duplicate error");
            }
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        users.put(updatedUser.getId(), updatedUser);
        log.debug("Пользователь с id: {} обновлен.", userId);
        return updatedUser;
    }

    public void delete(long userId) {
        isExist(userId);
        users.remove(userId);
        log.debug("Пользователь с id: {} удален.", userId);
    }

    private long getId() {
        return ++lastId;
    }

    public void isExist(long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id: {} не найден.", userId);
            throw new NotFoundException("User not found");
        }
    }

    private boolean checkEmail(User user) {
        return users.values()
                .stream().map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }

}