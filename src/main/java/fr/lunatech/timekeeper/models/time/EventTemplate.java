package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.Organization;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * An EventTemplate is an event, created manually by an administrator that would concern one
 * to many users. These events are “company events” like conference, hack-breakfast, training,
 * trip, all-staff-meeting...
 * @see https://lunatech.atlassian.net/wiki/spaces/T/pages/1948057763/Domain+model
 */
@Entity
@Table(name = "event_template")
public class EventTemplate extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    public String name;

    @NotNull
    public String description;

    @ManyToOne
    public Organization organization;

    @NotNull
    public LocalDateTime startDateTime;

    @Null
    public LocalDateTime endDateTime;

}
