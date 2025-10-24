package GestioneEventi.Service;

import GestioneEventi.Entities.Event;
import GestioneEventi.Entities.User;
import GestioneEventi.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event, User organizer) {
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event date must be in the future");
        }

        event.setOrganizer(organizer);
        event.setBookedSpots(0);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEventsOrderByDate(LocalDateTime.now());
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findUpcomingEventsByOrganizer(organizer, LocalDateTime.now());
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Event event, User organizer) {
        if (event.getId() == null) {
            throw new RuntimeException("Event ID is required for update");
        }

        Optional<Event> existingEvent = eventRepository.findById(event.getId());
        if (existingEvent.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Event eventToUpdate = existingEvent.get();

        // Check if the organizer is the owner of the event
        if (!eventToUpdate.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("You can only update your own events");
        }

        // Check if event date is in the future
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Event date must be in the future");
        }

        // Check if new available spots is not less than current booked spots
        if (event.getAvailableSpots() < eventToUpdate.getBookedSpots()) {
            throw new RuntimeException("Available spots cannot be less than current booked spots");
        }

        eventToUpdate.setTitle(event.getTitle());
        eventToUpdate.setDescription(event.getDescription());
        eventToUpdate.setEventDate(event.getEventDate());
        eventToUpdate.setLocation(event.getLocation());
        eventToUpdate.setAvailableSpots(event.getAvailableSpots());

        return eventRepository.save(eventToUpdate);
    }

    public void deleteEvent(Long id, User organizer) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        // Check if the organizer is the owner of the event
        if (!event.get().getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("You can only delete your own events");
        }

        eventRepository.deleteById(id);
    }

    public List<Event> searchEvents(String searchTerm) {
        return eventRepository.searchEvents(searchTerm, searchTerm, searchTerm);
    }

    public boolean isEventAvailable(Event event) {
        return event.hasAvailableSpots() && event.getEventDate().isAfter(LocalDateTime.now());
    }

    public void incrementBookedSpots(Event event) {
        event.incrementBookedSpots();
        eventRepository.save(event);
    }

    public void decrementBookedSpots(Event event) {
        event.decrementBookedSpots();
        eventRepository.save(event);
    }
}