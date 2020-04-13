package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "members")
public class Member extends PanacheEntity {

    @OneToOne
    @NotNull
    public User user;
    @NotNull
    public Role role;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    @NotNull
    public Activity activity;
}
