package fr.lunatech.timekeeper.models.time;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "timesheets")
public class TimeSheet extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    public Project project;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
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
    public TimeUnit durationUnit; // DAYS

    // -------------------------------
    @OneToMany(mappedBy = "timeSheet")
    public List<TimeEntry> entries;

    public TimeSheet() {}

    public TimeSheet(@NotNull Project project, User owner, TimeUnit timeUnit, Boolean defaultIsBillable, @Null LocalDate expirationDate, @Null Integer maxDuration, @Null TimeUnit durationUnit, List<TimeEntry> entries) {
        this.project = project;
        this.owner = owner;
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
        this.entries = entries;
    }
}
