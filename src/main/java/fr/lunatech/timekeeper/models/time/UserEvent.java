package fr.lunatech.timekeeper.models.time;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

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
public class UserEvent extends PanacheEntity {
    @NotNull
    public String description;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

    public LocalDate getDay() {
        return startDateTime.toLocalDate();
    }

}
