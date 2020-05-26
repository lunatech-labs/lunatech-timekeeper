package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_entry")
public class TimeEntry extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Boolean billable;

    @Column(name = "comment", length = 255)
    public String comment;

    @ManyToOne(targetEntity = TimeSheet.class, cascade = CascadeType.DETACH)
    public TimeSheet timeSheet;

    @NotNull
    public LocalDateTime startDateTime;

    @NotNull
    public LocalDateTime endDateTime;

}
