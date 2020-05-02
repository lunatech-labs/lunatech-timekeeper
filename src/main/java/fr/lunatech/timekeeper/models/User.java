package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User extends PanacheEntity {

    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    @NotBlank
    @Email
    public String email;

    @NotNull
    public String picture;

    @Column
    @Convert(converter = Profile.ListConverter.class)
    @NotEmpty
    public List<Profile> profiles;

    @OneToMany(mappedBy = "user")
    @NotNull
    public List<ProjectUser> projects;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull
    public Organization organization;


    public String getFullName() {
        return firstName + " " + lastName;
    }
}
