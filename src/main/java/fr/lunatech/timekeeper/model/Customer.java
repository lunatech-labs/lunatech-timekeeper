package fr.lunatech.timekeeper.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Customer extends PanacheEntity {

    public String name;
    public String description;
    @OneToMany
    public List<Activity> activities;

    public Customer() {
    }

}
