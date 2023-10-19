package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Override
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
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
        return BookingMapper.toBookingDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotOwnerException("Пользователь не найден");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findAllBookingsForOwner(long userId, BookingState state, PageRequest page) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        final LocalDateTime time = LocalDateTime.now();
        Collection<Booking> bookings;
        switch (state) {
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, time, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, time, page);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.toString()), page);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(userId, page);
                break;
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Transactional
    @Override
    public BookingDto updateStatusBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_BOOKING, bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь не найден");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotAvailableException("Бронирование уже одобрено или отклонено");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> findBookingForAllOwnerItems(long userId, BookingState state, PageRequest page) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
        Collection<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findAllForOwnerPast(userId, time, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllForOwnerCurrent(userId, time, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllForOwnerFuture(userId, time, page);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findAllForOwnerState(BookingStatus.valueOf(state.toString()), userId, page);
                break;
            default:
                bookings = bookingRepository.findAllForOwner(userId, page);
                break;
        }
        return BookingMapper.toBookingDto(bookings);
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

    private boolean isTimeOverlap(LocalDateTime startBooked, LocalDateTime endBooked, LocalDateTime startNew, LocalDateTime endNew) {
        return startBooked.isBefore(endNew) && startNew.isBefore(endBooked);
    }

}
