package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    Booking createBooking(long userId, BookingDto bookingDto);

    Booking findBooking(long userId, long bookingId);

    Collection<Booking> findAllBookingsForOwner(long userId, BookingState state);

    Booking updateStatusBooking(long userId, long bookingId, boolean approved);

    void deleteBookingById(long userId, long bookingId);

    Collection<Booking> findBookingForAllOwnerItems(long userId, BookingState state);

}
