package fr.lunatech.timekeeper.model;

import fr.lunatech.timekeeper.model.enums.DurationType;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "durationType", discriminatorType = DiscriminatorType.STRING)
public class Entry extends PanacheEntity {

    public Entry() {
    }


    private ZonedDateTime startDateTime;
    private ZonedDateTime stopDateTime;
    private long duration;
    private DurationType durationType;
    @OneToOne
    private Activity activity;
    @OneToOne
    private Task task;

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ZonedDateTime getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(ZonedDateTime stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
