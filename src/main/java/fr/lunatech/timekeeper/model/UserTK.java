package fr.lunatech.timekeeper.model;

import fr.lunatech.timekeeper.model.enums.Profil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class UserTK extends PanacheEntity {

    private String firstName;
    private String lastname;
    private String email;
    private Profil profile;

    public UserTK() {
    }

    public Profil getProfile() {
        return profile;
    }

    public void setProfile(Profil profile) {
        this.profile = profile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
