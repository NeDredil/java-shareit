package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    public static final long ID = 1L;

    private UserService userService;
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(1L, "userTest", "userTest@yandex.ru");
    }

    @Test
    void createUserTest() {
        User savedUser = new User();
        savedUser.setName(user.getName());
        savedUser.setEmail(user.getEmail());
        UserDto savedDto = UserMapper.toUserDto(savedUser);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto userDto = userService.createUser(savedDto);

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals(savedUser.getName(), userDto.getName());
        assertEquals(savedUser.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        user.setName("updated name");
        UserDto inputDto = UserMapper.toUserDto(user);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.updateUser(inputDto);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.findUserById(ID);

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void deleteUserByIdTest() {

        when(userRepository.existsById(0L)).thenReturn(true);
        userService.deleteUserById(0L);
        verify(userRepository, times(1)).deleteById(0L);
    }

    @Test
    public void testDeleteUserByIdWhenUserDoesNotExistThenNotFoundException() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        List<UserDto> dtos = userService.findAllUsers();

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(user.getId(), dtos.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }
}