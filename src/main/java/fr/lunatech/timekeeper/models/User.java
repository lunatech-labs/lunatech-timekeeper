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
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank
    public String firstName;
    @NotBlank
    public String lastName;
    @NotBlank
    @Email
    @NaturalId
    public String email;
    @NotNull
    public String picture;
    @Column
    @Convert(converter = Profile.ListConverter.class)
    @NotEmpty
    public List<Profile> profiles;
    @OneToMany(mappedBy = "user")
    @NotNull
    public List<Member> members;
    @ManyToOne
    @JoinColumn(name = "organisation_id", nullable = false)
    @NotNull
    public Organisation organisation;
}
