package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Profile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class UserResponse {

    @NotNull
    private final Long id;
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @NotBlank
    @Email
    private final String email;
    @NotEmpty
    private final List<Profile> profiles;

    public UserResponse(@NotNull Long id, @NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email, @NotEmpty List<Profile> profiles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profiles = profiles;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
}
