package fr.lunatech.timekeeper.users;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "USERS")
public class UserEntity extends PanacheEntity {
    @NotEmpty
    public String firstName;
    @NotEmpty
    public String lastName;
    @NotEmpty
    public String email;
    @Column
    @Convert(converter = Profile.ListConverter.class)
    @NotNull
    public List<Profile> profiles;
}
