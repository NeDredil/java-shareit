package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingStateMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Validated BookingDto bookingDto) {
        log.debug("поступил запрос на бронирование");
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, bookingDto));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable long bookingId) {
        log.debug("поступил запрос на получение бронирования с id: {} ", bookingId);
        return BookingMapper.toBookingDto(bookingService.findBookung(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        log.debug("поступил запрос на получение списка всех бронирований пользователя с id: {}  ", userId);
        return bookingService.findAllBookingsForOwner(userId, BookingStateMapper.toBookingState(state))
                .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable long bookingId,
                             @RequestParam Boolean approved) {
        log.debug("поступил запрос на редактирование бронирования id: {} владельцем c id: {} ",bookingId, userId);
        return BookingMapper.toBookingDto(bookingService.updateStatusBooking(userId, bookingId, approved));
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable long bookingId) {
        log.debug("поступил запрос на удаление бронирования c id: {} ", bookingId);
        bookingService.deleteBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findBookingForAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        log.debug("поступил запрос на получение списка бронирований для всех вещей пользователя с id: {}  ", userId);
        return bookingService.findBookingForAllOwnerItems(userId, BookingStateMapper.toBookingState(state))
                .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
