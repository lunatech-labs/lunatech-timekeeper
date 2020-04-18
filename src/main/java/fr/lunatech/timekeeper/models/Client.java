package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client extends PanacheEntity {

    @NotBlank
    public String name;
    @NotNull
    public String description;
    @OneToMany(mappedBy = "client")
    @NotNull
    public List<Project> projects;
}
