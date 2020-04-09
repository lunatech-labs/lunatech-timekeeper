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
    private UserTK user;
    private Role role;

    public UserTK getUser() {
        return user;
    }

    public void setUser(UserTK user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
