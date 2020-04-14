package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "durationType", discriminatorType = DiscriminatorType.STRING)
public class Entry extends PanacheEntity {

    public Entry() {
    }


    public ZonedDateTime startDateTime;
    public ZonedDateTime stopDateTime;
    public long duration;
    public DurationType durationType;
    @OneToOne
    public Activity activity;
    @OneToOne
    public Task task;


}
