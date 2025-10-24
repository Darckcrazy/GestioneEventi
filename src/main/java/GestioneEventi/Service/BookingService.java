package GestioneEventi.Service;

import GestioneEventi.Entities.Booking;
import GestioneEventi.Entities.Event;
import GestioneEventi.Entities.User;
import GestioneEventi.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventService eventService;

    public Booking createBooking(User user, Event event) {
        // Check if event is available
        if (!eventService.isEventAvailable(event)) {
            throw new RuntimeException("Event is not available for booking");
        }

        // Check if user already has a booking for this event
        if (bookingRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("You already have a booking for this event");
        }

        Booking booking = new Booking(user, event);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // Increment booked spots in the event
        eventService.incrementBookedSpots(event);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findConfirmedBookingsByUser(user);
    }

    public List<Booking> getBookingsByEvent(Event event) {
        return bookingRepository.findConfirmedBookingsByEvent(event);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> findByUserAndEvent(User user, Event event) {
        return bookingRepository.findByUserAndEvent(user, event);
    }

    public Booking cancelBooking(Long bookingId, User user) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOpt.get();

        // Check if the user owns this booking
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        // Check if booking can be cancelled (event not started yet)
        if (booking.getEvent().getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel booking for past events");
        }

        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);

        // Decrement booked spots in the event
        eventService.decrementBookedSpots(booking.getEvent());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId, User user) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOpt.get();

        // Check if the user owns this booking
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own bookings");
        }

        // If booking is confirmed, decrement booked spots
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            eventService.decrementBookedSpots(booking.getEvent());
        }

        bookingRepository.deleteById(bookingId);
    }

    public boolean hasUserBookedEvent(User user, Event event) {
        return bookingRepository.existsByUserAndEvent(user, event);
    }

    public long getConfirmedBookingsCount(Event event) {
        return bookingRepository.countConfirmedBookingsByEvent(event);
    }
}