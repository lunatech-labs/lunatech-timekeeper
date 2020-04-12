package fr.lunatech.timekeeper.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Collections.emptyList;

@Entity
@Table(name = "ACTIVITIES")
public class ActivityEntity extends PanacheEntity {

    @NotEmpty
    public String name;
    @NotNull
    public Boolean billable;
    public String description;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    public CustomerEntity customer;
    @OneToMany(mappedBy = "activity")
    @NotNull
    public List<MemberEntity> members = emptyList();
}
