package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Valid
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        log.debug("поступил запрос на бронирование, от пользователя с id: {}", userId);
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, bookingDto));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                  @PathVariable long bookingId) {
        log.debug("поступил запрос на получение бронирования с id: {} от пользователя с id: {} ", bookingId, userId);
        return BookingMapper.toBookingDto(bookingService.findBooking(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsForOwner(@RequestHeader(SHARER_USER_ID) Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "10") @Positive int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр 'from' должен быть больше или равен 0");
        }
        log.debug("поступил запрос на получение списка всех бронирований с состоянием {} " +
                "от пользователя с id: {}  ", state, userId);
        return bookingService.findAllBookingsForOwner(userId, BookingState.toBookingState(state), from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam Boolean approved) {
        log.debug("поступил запрос на редактирование бронирования id: {} владельцем c id: {} ", bookingId, userId);
        return BookingMapper.toBookingDto(bookingService.updateStatusBooking(userId, bookingId, approved));
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBookingById(@RequestHeader(SHARER_USER_ID) Long userId,
                                  @PathVariable long bookingId) {
        log.debug("поступил запрос на удаление бронирования c id: {} от пользователя с id: {} ", bookingId, userId);
        bookingService.deleteBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingForAllOwnerItems(@RequestHeader(SHARER_USER_ID) Long userId,
                                                              @RequestParam(defaultValue = "ALL") String state,
                                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                              @RequestParam(defaultValue = "10") @Positive int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр 'from' должен быть больше или равен 0");
        }
        log.debug("поступил запрос на получение списка бронирований для всех вещей с состоянием {} " +
                "от пользователя с id: {}  ", state, userId);
        return bookingService.findBookingForAllOwnerItems(userId, BookingState.toBookingState(state), from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
