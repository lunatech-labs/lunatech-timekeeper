package fr.lunatech.timekeeper.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Task extends PanacheEntity {

    public String name;
    public String category;

    public Task() {
    }


}
