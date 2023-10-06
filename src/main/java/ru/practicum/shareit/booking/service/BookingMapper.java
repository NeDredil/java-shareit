package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.service.UserMapper;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(UserMapper.toLittleUserDto(booking.getBooker()));
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setItem(ItemMapper.toShortItem(booking.getItem()));
        return bookingDto;
    }

    public static ShortBookingDto toLittleBookingDto(Booking booking) {
        ShortBookingDto shortBookingDto = new ShortBookingDto();
        shortBookingDto.setId(booking.getId());
        shortBookingDto.setBookerId(booking.getBooker().getId());
        return shortBookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

}
