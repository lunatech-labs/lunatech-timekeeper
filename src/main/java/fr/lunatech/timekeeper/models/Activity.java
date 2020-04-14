package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Collections.emptyList;

@Entity
@Table(name = "activities")
public class Activity extends PanacheEntity {

    @NotBlank
    public String name;
    @NotNull
    public Boolean billable;
    @NotNull
    public String description;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    public Customer customer;
    @OneToMany(mappedBy = "activity")
    @NotNull
    public List<Member> members = emptyList();
}