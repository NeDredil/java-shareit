package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) {
        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId)));
    }

    @Override
    public Collection<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userUpdate = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userDto.getId())));

        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        return userRepository.save(userUpdate);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        userRepository.deleteById(userId);
    }

}