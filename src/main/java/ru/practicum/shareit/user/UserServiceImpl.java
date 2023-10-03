package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public User createUser(UserDto userDto) {
        return userDao.createUser(UserMapper.toUser(userDto));
    }

    @Override
    public User findUserById(long userId) {
        return userDao.findUserById(userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User updateUser(long userId, UserDto userDto) {
        userDto.setId(userId);
        return userDao.updateUser(UserMapper.toUser(userDto));
    }

    @Override
    public void deleteUserById(long userId) {
        userDao.deleteUserById(userId);
    }

}