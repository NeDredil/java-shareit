package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDateTimeException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
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
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new IncorrectDateTimeException();
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
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        log.debug("Бронирование создано с id: {}.", saved.getId());
        return saved;
    }

    @Override
    public Booking findBookung(long userId, long bookingId) {
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
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException("Статус уже установлен.");
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

}
