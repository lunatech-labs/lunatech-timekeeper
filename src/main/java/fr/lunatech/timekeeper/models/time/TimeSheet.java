package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "timesheets")
public class TimeSheet extends PanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    public Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Null
    public User owner;

    @Enumerated(EnumType.STRING)
    public TimeUnit timeUnit;

    public Boolean defaultIsBillable;

    // 1---------------
    @Null
    public LocalDate expirationDate;

    // 2 ------------------
    @Null
    public Integer maxDuration; // eg 21

    @Null
    public String durationUnit; // DAYS

    // -------------------------------
    @OneToMany(mappedBy = "timeSheet")
    public List<TimeEntry> entries;

}
