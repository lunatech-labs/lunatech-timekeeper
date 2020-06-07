package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "organizations", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Organization extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

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

    @OneToMany(mappedBy = "organization")
    @NotNull
    public List<Project> clients;
}
