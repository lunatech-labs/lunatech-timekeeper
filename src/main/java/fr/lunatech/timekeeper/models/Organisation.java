package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Entity
@Table(name = "organisations")
public class Organisation extends PanacheEntity {

    @NotBlank
    public String name;
    @NotBlank
    public String tokenname;
    @OneToMany(mappedBy = "organisation")
    @NotNull
    public List<User> users;
    @OneToMany(mappedBy = "organisation")
    @NotNull
    public List<Project> projects;

}
