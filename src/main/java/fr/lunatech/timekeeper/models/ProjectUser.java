package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "projects_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "user_id"})})
public class ProjectUser extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    public Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    public User user;

    @NotNull
    public Boolean manager;

}
