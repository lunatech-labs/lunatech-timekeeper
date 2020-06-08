package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

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
        this.organizationTokenName = StringUtils.trimToEmpty(organizationTokenName);
    }

    public User unbind(@NotNull User user, @NotNull Function<String, Optional<Organization>> findOrganization) {
        user.firstName = getFirstName();
        user.lastName = getLastName();
        user.email = getEmail();
        user.picture = getPicture();
        user.profiles = getProfiles();
        user.organization = findOrganization.apply(getOrganizationTokenName())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Organization [%s] was not found in the DB", organizationTokenName)));

        return user;
    }

    public User unbind(@NotNull Function<String, Optional<Organization>> findOrganizationByTokenName) {
        return unbind(new User(), findOrganizationByTokenName);
    }

    /* Panache optimizes update process, but to avoid unnecessary logging, use this */
    public boolean isEquals(@NotNull User user) {
        return !(Objects.equals(user.firstName, getFirstName())
                && Objects.equals(user.lastName, getLastName())
                && Objects.equals(user.email, getEmail())
                && Objects.equals(user.picture, getPicture())
                && user.profiles.size() == getProfiles().size()
                && user.profiles.containsAll(getProfiles())
                && Objects.equals(user.organization.tokenName, getOrganizationTokenName())
        );
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
        return StringUtils.trimToEmpty(organizationTokenName);
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
