package fr.lunatech.timekeeper.activities;

import fr.lunatech.timekeeper.customers.CustomerEntity;
import fr.lunatech.timekeeper.members.MemberEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "ACTIVITIES")
public class ActivityEntity extends PanacheEntity {

    @NotEmpty
    public String name;
    @NotNull
    public Boolean billable;
    public String description;
    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    public CustomerEntity customer;
    @OneToMany(mappedBy="activity")
    public List<MemberEntity> members;

}
