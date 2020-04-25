package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntity {

    @NotBlank
    public String name;
    @NotNull
    public Boolean billable;
    @NotNull
    public String description;
    @ManyToOne
    @JoinColumn(name = "client_id")
    @Null
    public Client client;
    @OneToMany(mappedBy = "project")
    @NotNull
    public List<Member> members;
}