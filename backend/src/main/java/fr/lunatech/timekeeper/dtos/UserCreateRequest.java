package fr.lunatech.timekeeper.dtos;

import fr.lunatech.timekeeper.models.Profile;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class UserCreateRequest {

    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @NotBlank
    @Email
    private final String email;
    @NotNull
    private final List<Profile> profiles;

    @JsonbCreator
    public UserCreateRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email, @NotNull List<Profile> profiles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profiles = profiles;
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
