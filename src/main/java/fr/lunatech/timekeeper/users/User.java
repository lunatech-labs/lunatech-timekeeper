package fr.lunatech.timekeeper.users;

import javax.validation.constraints.NotNull;
import java.util.List;

public class User extends UserMutable {

    @NotNull
    private Long id;

    public User() {
        super();
    }

    public User(Long id, String firstName, String lastName, String email, List<Profile> profiles) {
        super(firstName, lastName, email, profiles);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
