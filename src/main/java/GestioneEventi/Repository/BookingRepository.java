package GestioneEventi.Repository;

import GestioneEventi.Entities.Booking;
import GestioneEventi.Entities.User;
import GestioneEventi.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByEvent(Event event);

    Optional<Booking> findByUserAndEvent(User user, Event event);

    boolean existsByUserAndEvent(User user, Event event);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event = :event AND b.status = 'CONFIRMED'")
    long countConfirmedBookingsByEvent(@Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByUser(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.event = :event AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByEvent(@Param("event") Event event);
}