package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "organizations", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Organization extends PanacheEntity {

    @NotBlank
    public String name;

    @NotBlank
    public String tokenName;

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<User> users;

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<Project> projects;

}
