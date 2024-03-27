package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

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

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("Test@yandex.ru");
        User savedUser = userRepository.save(user);

        userRepository.delete(savedUser);

        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        Assertions.assertNull(deletedUser);
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setName("Test1");
        user1.setEmail("test1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Test2");
        user2.setEmail("test2@example.com");
        userRepository.save(user2);

        List<User> userList = userRepository.findAll();

        Assertions.assertEquals(2, userList.size());
        Assertions.assertTrue(userList.contains(user1));
        Assertions.assertTrue(userList.contains(user2));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("Test@yandex.ru");
        User savedUser = userRepository.save(user);

        savedUser.setName("UpdatedTest");
        savedUser.setEmail("updatedtest@yandex.ru");
        User updatedUser = userRepository.save(savedUser);

        Assertions.assertEquals(savedUser.getId(), updatedUser.getId());
        Assertions.assertEquals("UpdatedTest", updatedUser.getName());
        Assertions.assertEquals("updatedtest@yandex.ru", updatedUser.getEmail());
    }
}