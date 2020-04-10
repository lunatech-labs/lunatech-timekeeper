package fr.lunatech.timekeeper.model;

import fr.lunatech.timekeeper.model.enums.Role;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Member extends PanacheEntity {

    public Member() {
    }

    @OneToOne
    public UserTK user;
    public Role role;


}
