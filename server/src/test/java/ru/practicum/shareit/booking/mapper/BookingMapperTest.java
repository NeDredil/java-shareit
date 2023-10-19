package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    public void testToBookingDto() {
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

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals(item.getId(), bookingDto.getItemId());
    }

    @Test
    public void testToLittleBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);

        User booker = new User();
        booker.setId(2L);

        booking.setBooker(booker);

        ShortBookingDto shortBookingDto = BookingMapper.toLittleBookingDto(booking);

        assertEquals(booking.getId(), shortBookingDto.getId());
        assertEquals(booker.getId(), shortBookingDto.getBookerId());
    }

    @Test
    public void testToBooking() {
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
    }

    @Test
    public void testToBookingDtoWhenValidBookingThenMapsToBookingDto() {
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

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals(item.getId(), bookingDto.getItemId());
    }

    @Test
    public void testToLittleBookingDtoWhenValidBookingThenMapsToShortBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);

        User booker = new User();
        booker.setId(2L);

        booking.setBooker(booker);

        ShortBookingDto shortBookingDto = BookingMapper.toLittleBookingDto(booking);

        assertEquals(booking.getId(), shortBookingDto.getId());
        assertEquals(booker.getId(), shortBookingDto.getBookerId());
    }

    @Test
    public void testToBookingWhenValidBookingDtoThenMapsToBooking() {
        BookingDto bookingDto = new BookingDto();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
    }

    @Test
    public void testToBookingDtoCollection() {
        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(3L);

        Collection<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.of(2023, 10, 17, 13, 43, 10));
        booking1.setEnd(LocalDateTime.of(2023, 10, 17, 14, 43, 10));
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.of(2023, 10, 18, 10, 0, 0));
        booking2.setEnd(LocalDateTime.of(2023, 10, 18, 11, 0, 0));

        bookings.add(booking1);
        bookings.add(booking2);


        Collection<BookingDto> bookingDtos = BookingMapper.toBookingDto(bookings);

        assertEquals(2, bookingDtos.size());

        List<BookingDto> bookingDtoList = new ArrayList<>(bookingDtos);
        BookingDto bookingDto1 = bookingDtoList.get(0);
        assertEquals(1L, bookingDto1.getId());
        assertEquals(LocalDateTime.of(2023, 10, 17, 13, 43, 10), bookingDto1.getStart());
        assertEquals(LocalDateTime.of(2023, 10, 17, 14, 43, 10), bookingDto1.getEnd());

        BookingDto bookingDto2 = bookingDtoList.get(1);
        assertEquals(2L, bookingDto2.getId());
        assertEquals(LocalDateTime.of(2023, 10, 18, 10, 0, 0), bookingDto2.getStart());
        assertEquals(LocalDateTime.of(2023, 10, 18, 11, 0, 0), bookingDto2.getEnd());
    }
}

