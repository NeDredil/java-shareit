package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllBookingsForOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatusBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable long bookingId,
                                                      @RequestParam String approved) {
        log.info("Update status for booking {}, userId={}, status={}", bookingId, userId, approved);
        return bookingClient.updateStatusBooking(userId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable long bookingId) {
        log.info("Delete booking {}, userId={}", bookingId, userId);
        return bookingClient.deleteBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingForAllOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking for owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingForAllOwnerItems(userId, state, from, size);
    }

}
