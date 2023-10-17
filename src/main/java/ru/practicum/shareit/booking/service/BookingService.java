package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto findBooking(long userId, long bookingId);

    Collection<BookingDto> findAllBookingsForOwner(long userId, BookingState state, PageRequest page);

    BookingDto updateStatusBooking(long userId, long bookingId, boolean approved);

    void deleteBookingById(long userId, long bookingId);

    Collection<BookingDto> findBookingForAllOwnerItems(long userId, BookingState state, PageRequest page);

}
