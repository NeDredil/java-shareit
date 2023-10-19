package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItem(Item item);

    List<Booking> findAllByItemIdIn(Set<Long> ids);

    Collection<Booking> findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(Long bookerId, Long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findAllByItemIdInAndStatus(Set<Long> ids, BookingStatus status);

    List<Booking> findAllByBookerId(Long booker, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfter(Long booker, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long booker, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndEndBefore(Long booker, LocalDateTime time, Pageable page);

    List<Booking> findAllByBookerIdAndStatus(Long booker, BookingStatus state, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start DESC")
    List<Booking> findAllForOwner(Long owner, Pageable page);

    @Query("select b from Booking b " +
            "where b.status = ?1 " +
            "and b.item.owner.id = ?2 " +
            "order by b.start DESC")
    List<Booking> findAllForOwnerState(BookingStatus state, Long owner, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start DESC")
    List<Booking> findAllForOwnerPast(Long itemId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start DESC")
    List<Booking> findAllForOwnerCurrent(Long itemId, LocalDateTime time, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start DESC")
    List<Booking> findAllForOwnerFuture(Long itemId, LocalDateTime time, Pageable page);

}
