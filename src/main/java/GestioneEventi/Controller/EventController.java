package GestioneEventi.Controller;

import GestioneEventi.DTO.EventRequest;
import GestioneEventi.Entities.Event;
import GestioneEventi.Entities.User;
import GestioneEventi.Service.EventService;
import GestioneEventi.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Event>> getAvailableEvents() {
        List<Event> events = eventService.getAvailableEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String query) {
        List<Event> events = eventService.searchEvents(query);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Event>> getMyEvents(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Event> events = eventService.getEventsByOrganizer(user);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.findById(id);
        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequest request,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            // Check if user is an organizer
            if (user.getRole() != User.Role.ORGANIZZATORE_EVENTI) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only event organizers can create events");
            }

            Event event = new Event(request.getTitle(), request.getDescription(),
                    request.getEventDate(), request.getLocation(),
                    request.getAvailableSpots(), user);

            Event savedEvent = eventService.createEvent(event, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id,
                                         @Valid @RequestBody EventRequest request,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            // Check if user is an organizer
            if (user.getRole() != User.Role.ORGANIZZATORE_EVENTI) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only event organizers can update events");
            }

            Optional<Event> existingEvent = eventService.findById(id);
            if (existingEvent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Event event = existingEvent.get();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setEventDate(request.getEventDate());
            event.setLocation(request.getLocation());
            event.setAvailableSpots(request.getAvailableSpots());

            Event updatedEvent = eventService.updateEvent(event, user);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            // Check if user is an organizer
            if (user.getRole() != User.Role.ORGANIZZATORE_EVENTI) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only event organizers can delete events");
            }

            eventService.deleteEvent(id, user);
            return ResponseEntity.ok().body("Event deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}