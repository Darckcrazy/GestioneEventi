package GestioneEventi.Repository;

import GestioneEventi.Entities.User;
import GestioneEventi.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizer(User organizer);

    List<Event> findByEventDateAfter(LocalDateTime date);

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :currentDate AND e.bookedSpots < e.availableSpots")
    List<Event> findAvailableEvents(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :currentDate AND e.bookedSpots < e.availableSpots ORDER BY e.eventDate ASC")
    List<Event> findAvailableEventsOrderByDate(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT e FROM Event e WHERE e.organizer = :organizer AND e.eventDate > :currentDate")
    List<Event> findUpcomingEventsByOrganizer(@Param("organizer") User organizer, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT e FROM Event e WHERE e.title LIKE %:title% OR e.description LIKE %:description% OR e.location LIKE %:location%")
    List<Event> searchEvents(@Param("title") String title, @Param("description") String description, @Param("location") String location);
}