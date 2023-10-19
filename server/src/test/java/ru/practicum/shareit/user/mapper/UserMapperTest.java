package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class UserMapperTest {

    @Test
    public void testToUserDto() {
        User user = User.builder()
                .id(1)
                .name("User Test")
                .email("test@yandex.ru")
                .build();

        UserDto userDto = UserMapper.toUserDto(user);

        Assertions.assertEquals(user.getId(), userDto.getId());
        Assertions.assertEquals(user.getName(), userDto.getName());
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void testToUser() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("User Test")
                .email("test@yandex.ru")
                .build();

        User user = UserMapper.toUser(userDto);

        Assertions.assertEquals(userDto.getId(), user.getId());
        Assertions.assertEquals(userDto.getName(), user.getName());
        Assertions.assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void testToLittleUserDto() {
        User user = User.builder()
                .id(1)
                .name("User Test")
                .email("test@yandex.ru")
                .build();

        ShortUserDto shortUserDto = UserMapper.toLittleUserDto(user);

        Assertions.assertEquals(user.getId(), shortUserDto.getId());
        Assertions.assertEquals(user.getName(), shortUserDto.getName());
    }

    @Test
    public void testListToUserDto() {
        User user1 = User.builder()
                .id(1)
                .name("User Test")
                .email("test@yandex.ru")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("User Test2")
                .email("test2@yandex.ru")
                .build();
        List<User> userList = Arrays.asList(user1, user2);

        Collection<UserDto> userDtoList = UserMapper.listToUserDto(userList);

        Assertions.assertEquals(userList.size(), userDtoList.size());
        for (UserDto userDto : userDtoList) {
            User user = userList.stream()
                    .filter(u -> u.getId() == userDto.getId())
                    .findFirst()
                    .orElse(null);
            Assertions.assertNotNull(user);
            Assertions.assertEquals(user.getName(), userDto.getName());
            Assertions.assertEquals(user.getEmail(), userDto.getEmail());
        }
    }
}