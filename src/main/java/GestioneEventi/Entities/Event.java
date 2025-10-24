package GestioneEventi.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Date is required")
    @Future(message = "Event date must be in the future")
    @Column(nullable = false)
    private LocalDateTime eventDate;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Available spots is required")
    @Min(value = 1, message = "Available spots must be at least 1")
    @Column(nullable = false)
    private Integer availableSpots;

    @Column(nullable = false)
    private Integer bookedSpots = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Event() {}

    public Event(String title, String description, LocalDateTime eventDate, String location,
                 Integer availableSpots, User organizer) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.availableSpots = availableSpots;
        this.organizer = organizer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public Integer getBookedSpots() {
        return bookedSpots;
    }

    public void setBookedSpots(Integer bookedSpots) {
        this.bookedSpots = bookedSpots;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean hasAvailableSpots() {
        return bookedSpots < availableSpots;
    }

    public Integer getRemainingSpots() {
        return availableSpots - bookedSpots;
    }

    public void incrementBookedSpots() {
        this.bookedSpots++;
    }

    public void decrementBookedSpots() {
        if (this.bookedSpots > 0) {
            this.bookedSpots--;
        }
    }
}