package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public User create(User user) {
        return userDao.create(user);
    }

    @Override
    public User read(long userId) {
        return userDao.read(userId);
    }

    @Override
    public Collection<User> readAll() {
        return userDao.readAll();
    }

    @Override
    public User update(long userId, User user) {
        return userDao.update(userId, user);
    }

    @Override
    public void delete(long userId) {
        userDao.delete(userId);
    }

}