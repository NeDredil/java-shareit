package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("testuser@example.com")
                .build();
    }

    @Test
    public void testCreateUserWhenValidUserDtoThenUserCreatedAndSaved() {
        UserDto userDto = new UserDto(1L, "Test", "Test@yandex.ru");
        User user = new User(1L, "Test", "Test@yandex.ru");

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        userService.createUser(userDto);

        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    public void testFindUserByIdWhenValidIdThenUserDtoReturned() {
        User user = new User(1L, "Test", "Test@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(1L);

        assertEquals(user, foundUser);
    }

    @Test
    public void testFindAllUsersThenAllUserDtosReturned() {
        User user1 = new User(1L, "Test", "Test@yandex.ru");
        User user2 = new User(2L, "Test", "Test@yandex.ru");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        Collection<User> foundUsers = userService.findAllUsers();

        assertEquals(users, foundUsers);
    }

    @Test
    public void testUpdateUserWhenValidUserDtoThenUserUpdated() {
        UserDto userDto = new UserDto(1L, "updateTest", "updateTest@yandex.ru");
        User user = new User(1L, "Test", "Test@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        userService.updateUser(userDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void testUpdateUserNameWhenValidUserDtoThenUserNameUpdated() {
        UserDto userDto = new UserDto(1L, "updateTest", null);
        User user = new User(1L, "Test", "Test@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        userService.updateUser(userDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    public void testUpdateUserEmailWhenValidUserDtoThenUserNameUpdated() {
        UserDto userDto = new UserDto(1L, null, "updateTest@yandex.ru");
        User user = new User(1L, "Test", "Test@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        userService.updateUser(userDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void testDeleteUserByIdWhenUserIsDeletedSuccessfullyThenReturnVoid() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteUserByIdWhenUserIsNotFoundThenThrowNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(1L));
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testToLittleUserDto() {
        User user = new User(1L, "Test", "Test@yandex.ru");

        ShortUserDto shortUserDto = UserMapper.toLittleUserDto(user);

        assertEquals(user.getId(), shortUserDto.getId());
        assertEquals(user.getName(), shortUserDto.getName());
        assertNotEquals(user.toString(), shortUserDto.toString());
    }

    @Test
    public void testToLittleUserDtoWhenValidUserThenReturnShortUserDto() {
        // Act
        ShortUserDto shortUserDto = UserMapper.toLittleUserDto(user);

        // Assert
        assertThat(shortUserDto).isNotNull();
        assertThat(shortUserDto.getId()).isEqualTo(user.getId());
        assertThat(shortUserDto.getName()).isEqualTo(user.getName());
    }
}
