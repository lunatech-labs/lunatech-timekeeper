package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * An Event is something that "block" a cell in your TimeSheet, so that one cannot enters TimeEntry for this
 * slot.
 * Kind of Event :
 * - vacations
 * - company event like HackBreakfast
 */
@Entity
@Table(name = "user_events")
public class UserEvent extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    public String name;

    @NotNull
    public String description;

    @Enumerated(EnumType.STRING)
    public EventType eventType;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

    @Null
    public LocalDate getDay() {
        if (startDateTime == null) {
            return null;
        }
        return startDateTime.toLocalDate();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    public EventTemplate eventTemplate;

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventType=" + eventType +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", owner=" + owner.getFullName() +
                '}';
    }
}
