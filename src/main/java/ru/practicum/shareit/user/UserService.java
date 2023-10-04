package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User createUser(UserDto userDto);

    User findUserById(long userId);

    Collection<User> findAllUsers();

    User updateUser(UserDto userDto);

    void deleteUserById(long userId);

}