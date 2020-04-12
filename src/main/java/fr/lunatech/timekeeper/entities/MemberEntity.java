package fr.lunatech.timekeeper.entities;

import fr.lunatech.timekeeper.models.Role;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MEMBERS")
public class MemberEntity extends PanacheEntity {

    @OneToOne
    public UserEntity user;
    @NotNull
    public Role role;
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    public ActivityEntity activity;
}
