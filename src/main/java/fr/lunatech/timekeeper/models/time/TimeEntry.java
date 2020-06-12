package fr.lunatech.timekeeper.models.time;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "timeentries")
public class TimeEntry extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "comment", length = 255)
    public String comment;

    @ManyToOne
    @JoinColumn(name = "timeSheet_id", nullable = false)
    public TimeSheet timeSheet;

    @NotNull
    public LocalDateTime startDateTime;

    @NotNull
    public LocalDateTime endDateTime;

}
