package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("Test@yandex.ru");
        User savedUser = userRepository.save(user);
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setName("Test");
        user.setEmail("Test@yandex.ru");
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(savedUser.getId(), foundUser.getId());
        Assertions.assertEquals(savedUser.getName(), foundUser.getName());
        Assertions.assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

}