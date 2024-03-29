package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("Test@yandex.ru")
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void testGetItemsBySearchQuery() {
        List<Item> searched = itemRepository.getItemsBySearchQuery("DeSc", Pageable.unpaged());

        assertEquals(List.of(item), searched, "Expected and actual lists are not equal");
    }

    @Test
    void testSearchWhenNotExistParam() {
        List<Item> searched = itemRepository.getItemsBySearchQuery("item", Pageable.unpaged());

        assertTrue(searched.isEmpty());
    }

    @Test
    void findAllByOwnerId() {
        List<Item> allByOwnerId = itemRepository.findAllByOwnerId(user.getId(), Pageable.unpaged());

        assertEquals(List.of(item), allByOwnerId, "Expected and actual lists are not equal");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}