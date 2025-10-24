package GestioneEventi.Controller;

import GestioneEventi.Entities.Booking;
import GestioneEventi.Entities.Event;
import GestioneEventi.Entities.User;
import GestioneEventi.Service.BookingService;
import GestioneEventi.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventService eventService;

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Booking> bookings = bookingService.getBookingsByUser(user);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<? extends Object> getBookingsByEvent(@PathVariable Long eventId,
                                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Optional<Event> event = eventService.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user is the organizer of the event
        if (!event.get().getOrganizer().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only view bookings for your own events");
        }

        List<Booking> bookings = bookingService.getBookingsByEvent(event.get());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Optional<Booking> booking = bookingService.findById(id);
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns the booking or is the organizer of the event
        if (!booking.get().getUser().getId().equals(user.getId()) &&
                !booking.get().getEvent().getOrganizer().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only view your own bookings");
        }

        return ResponseEntity.ok(booking.get());
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> createBooking(@PathVariable Long eventId,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            Optional<Event> event = eventService.findById(eventId);
            if (event.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingService.createBooking(user, event.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            Booking booking = bookingService.cancelBooking(id, user);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            bookingService.deleteBooking(id, user);
            return ResponseEntity.ok().body("Booking deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}