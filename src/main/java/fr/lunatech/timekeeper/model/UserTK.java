package fr.lunatech.timekeeper.model;

import fr.lunatech.timekeeper.model.enums.Profile;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class UserTK extends PanacheEntity {

    public String firstName;
    public String lastname;
    public String email;
    public Profile profile;

    public UserTK() {
    }

}
