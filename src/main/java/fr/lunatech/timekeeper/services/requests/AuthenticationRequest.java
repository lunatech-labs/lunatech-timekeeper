package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class AuthenticationRequest {

    @NotBlank
    private final String firstName;

    @NotBlank
    private final String lastName;

    @NotBlank
    @Email
    private final String email;

    @NotNull
    private final String picture;

    @NotEmpty
    private final List<Profile> profiles;

    @NotNull
    private final String organizationTokenName;

    @JsonbCreator
    public AuthenticationRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email,
            @NotNull String picture,
            @NotEmpty List<Profile> profiles,
            @NotNull String organizationTokenName
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.profiles = profiles;
        this.organizationTokenName = organizationTokenName;
    }

    public User unbind(@NotNull User user, @NotNull Function<String, Optional<Organization>> findOrganization) {
        user.firstName = getFirstName();
        user.lastName = getLastName();
        user.email = getEmail();
        user.picture = getPicture();
        user.profiles = getProfiles();
        user.organization = findOrganization.apply(getOrganizationTokenName())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown organization. organizationTokenName=%s", organizationTokenName)));

        return user;
    }

    public User unbind(@NotNull Function<String, Optional<Organization>> findOrganizationByTokenName) {
        return unbind(new User(), findOrganizationByTokenName);
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

    public String getOrganizationTokenName() {
        return organizationTokenName;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", profiles=" + profiles +
                ", organizationTokenName='" + organizationTokenName + '\'' +
                '}';
    }
}
