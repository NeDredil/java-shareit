package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByItemIdIn(Set<Long> ids);

    Collection<Booking> findAllByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long booker);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long booker, LocalDateTime time);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long booker, LocalDateTime time1, LocalDateTime time2);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long booker, LocalDateTime time);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long booker, BookingStatus state);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "order by b.start DESC")
    Collection<Booking> findAllForOwner(Long owner);

    @Query("select b from Booking b " +
            "where b.status =?1 " +
            "and b.item in (select i.id from Item i " +
            "where i.owner.id = ?2) " +
            "order by b.start DESC")
    Collection<Booking> findAllForOwnerState(BookingStatus state, Long owner);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.end <?2 " +
            "order by b.start DESC")
    Collection<Booking> findAllForOwnerPast(Long itemId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start <?2 " +
            "and b.end>?2  " +
            "order by b.start DESC")
    Collection<Booking> findAllForOwnerCurrent(Long itemId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item in (select i.id from Item i " +
            "where i.owner.id = ?1) " +
            "and b.start >?2 " +
            "order by b.start DESC")
    Collection<Booking> findAllForOwnerFuture(Long itemId, LocalDateTime time);

}
