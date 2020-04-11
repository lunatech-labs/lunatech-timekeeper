package fr.lunatech.timekeeper.customers;

import fr.lunatech.timekeeper.activities.ActivityEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.List;

@Entity
@Table(name = "CUSTOMERS")
public class CustomerEntity extends PanacheEntity {
    @NotEmpty
    public String name;
    public String description;
    @OneToMany(mappedBy="customer")
    public List<ActivityEntity> activities;
}
