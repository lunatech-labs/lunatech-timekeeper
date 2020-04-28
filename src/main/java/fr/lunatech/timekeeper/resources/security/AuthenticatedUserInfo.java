package fr.lunatech.timekeeper.resources.security;

import fr.lunatech.timekeeper.models.Profile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AuthenticatedUserInfo {
    final String firstName;
    @NotBlank
    final String lastName;
    @NotBlank
    @Email
    public final String email;
    @NotNull
    private final String picture;
    @NotEmpty
    private final List<Profile> profiles;
    @NotBlank
    private final String organization;

    public AuthenticatedUserInfo(String firstName,
                                 @NotBlank String lastName,
                                 @NotBlank @Email String email,
                                 @NotNull String picture,
                                 @NotEmpty List<Profile> profiles,
                                 @NotBlank String organization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.profiles = profiles;
        this.organization = organization;
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

    public String getPicture() {
        return picture;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public String getOrganization() {
        return organization;
    }
}
