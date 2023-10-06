package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static ShortUserDto toLittleUserDto(User user) {
        ShortUserDto userDto = new ShortUserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }
}