package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;

    private Item item;

    private Booking bookingCurrent;
    private Booking bookingPast;
    private Booking bookingFuture;

    @BeforeEach
    void setUp() {
        User owner = userRepository.save(User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build());

        booker = userRepository.save(User.builder()
                .name("booker")
                .email("booker@mail.ru")
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        bookingCurrent = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        bookingPast = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        bookingFuture = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());
    }

    @Test
    void findAllByItemIdIn() {
        Collection<Booking> allByItemIdIn = bookingRepository.findAllByItemIdIn(Set.of(item.getId()));

        Assertions.assertEquals(3, allByItemIdIn.size());
    }

    @Test
    void findAllByBookerId() {
        Collection<Booking> allByBookerId = bookingRepository.findAllByBookerId(booker.getId(), Pageable.unpaged());

        Assertions.assertEquals(3, allByBookerId.size());
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        Collection<Booking> allByBookerIdAndStartAfter = bookingRepository.findAllByBookerIdAndStartAfter(
                booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(1, allByBookerIdAndStartAfter.size());
        Assertions.assertEquals(List.of(bookingFuture), allByBookerIdAndStartAfter);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        Collection<Booking> allByBookerIdAndStartAfter = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(1, allByBookerIdAndStartAfter.size());
        Assertions.assertEquals(List.of(bookingCurrent), allByBookerIdAndStartAfter);
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        List<Booking> allByBookerIdAndEndBefore = bookingRepository.findAllByBookerIdAndEndBefore(
                booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(1, allByBookerIdAndEndBefore.size());
        Assertions.assertEquals(List.of(bookingPast), allByBookerIdAndEndBefore);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}