package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Item item;

    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .name("name")
                .email("Test@yandex.ru")
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .build());

        comment = commentRepository.save(Comment.builder()
                .text("comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByItemId() {
        Collection<Comment> allByItemId = commentRepository.findAllByItemId(item.getId());

        Assertions.assertEquals(List.of(comment), allByItemId);
    }

    @Test
    void findAllByItemIdIn() {
        Collection<Comment> allByItemId = commentRepository.findAllByItemIdIn(Set.of(item.getId()));

        Assertions.assertEquals(List.of(comment), allByItemId);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

}
