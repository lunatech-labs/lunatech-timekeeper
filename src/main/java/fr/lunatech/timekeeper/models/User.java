package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Collections.emptyList;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank
    public String firstName;
    @NotBlank
    public String lastName;
    @NotBlank
    @Email
    public String email;
    @Column
    @Convert(converter = Profile.ListConverter.class)
    @NotNull
    public List<Profile> profiles = emptyList();
}
