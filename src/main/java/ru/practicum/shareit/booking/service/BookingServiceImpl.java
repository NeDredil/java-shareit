package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.exception.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(long userId, BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new IncorrectDateTimeException("Неверно указана дата.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_ITEM, bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Не доступна для бронирования.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId)));
        if (user.getId() == item.getOwner().getId()) {
            throw new NotFoundException("Пользователь не является владельцем вещи.");
        }
        isBookingTimeAvailable(item, bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        log.debug("Бронирование создано с id: {}.", saved.getId());
        return saved;
    }

    @Override
    public Booking findBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new NotOwnerException("Пользователь не найден");
        }
    }

    @Override
    public Collection<Booking> findAllBookingsForOwner(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        final LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time);
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state.toString()));
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }
    }

    public Booking updateStatusBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не найден");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotAvailableException("Бронирование уже одобрено или отклонено");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getBooker().getId() != userId) {
            throw new NotOwnerException("Пользователь не найден");
        } else {
            bookingRepository.deleteById(bookingId);
        }
    }

    public Collection<Booking> findBookingForAllOwnerItems(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case PAST:
                return bookingRepository.findAllForOwnerPast(userId, time);
            case CURRENT:
                return bookingRepository.findAllForOwnerCurrent(userId, time);
            case FUTURE:
                return bookingRepository.findAllForOwnerFuture(userId, time);
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllForOwnerState(BookingStatus.valueOf(state.toString()), userId);
            default:
                return bookingRepository.findAllForOwner(userId);
        }
    }

    private boolean isBookingTimeAvailable(Item item, LocalDateTime start, LocalDateTime end) {
        Collection<Booking> existingBookings = bookingRepository.findByItem(item);

        for (Booking booking : existingBookings) {
            if (isTimeOverlap(booking.getStart(), booking.getEnd(), start, end)) {
                throw new BookingTimeUnavailableException("Время бронирования недоступно.");
            }
        }
        return true;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

}
