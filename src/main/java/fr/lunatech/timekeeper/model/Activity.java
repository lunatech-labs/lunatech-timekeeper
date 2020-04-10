package fr.lunatech.timekeeper.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Activity extends PanacheEntity {

    public String name;
    public Boolean billale;
    public String description;
    @ManyToOne
    public Customer customer;
    @OneToMany
    public List<Member> members;

    public Activity() {
    }

}
