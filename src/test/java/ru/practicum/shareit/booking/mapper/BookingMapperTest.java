package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    public void testToBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(3L);

        booking.setBooker(booker);
        booking.setItem(item);

        // Act
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // Assert
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals(item.getId(), bookingDto.getItemId());
    }

    @Test
    public void testToLittleBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);

        User booker = new User();
        booker.setId(2L);

        booking.setBooker(booker);

        // Act
        ShortBookingDto shortBookingDto = BookingMapper.toLittleBookingDto(booking);

        // Assert
        assertEquals(booking.getId(), shortBookingDto.getId());
        assertEquals(booker.getId(), shortBookingDto.getBookerId());
    }

    @Test
    public void testToBooking() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        // Act
        Booking booking = BookingMapper.toBooking(bookingDto);

        // Assert
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
    }

    @Test
    public void testToBookingDtoWhenValidBookingThenMapsToBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(3L);

        booking.setBooker(booker);
        booking.setItem(item);

        // Act
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // Assert
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals(item.getId(), bookingDto.getItemId());
    }

    @Test
    public void testToLittleBookingDtoWhenValidBookingThenMapsToShortBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);

        User booker = new User();
        booker.setId(2L);

        booking.setBooker(booker);

        // Act
        ShortBookingDto shortBookingDto = BookingMapper.toLittleBookingDto(booking);

        // Assert
        assertEquals(booking.getId(), shortBookingDto.getId());
        assertEquals(booker.getId(), shortBookingDto.getBookerId());
    }

    @Test
    public void testToBookingWhenValidBookingDtoThenMapsToBooking() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        // Act
        Booking booking = BookingMapper.toBooking(bookingDto);

        // Assert
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
    }
}

