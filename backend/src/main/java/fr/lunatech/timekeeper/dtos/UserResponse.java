package fr.lunatech.timekeeper.dtos;

import fr.lunatech.timekeeper.models.Profile;

import java.util.List;

public final class UserResponse {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<Profile> profiles;

    public UserResponse(Long id, String firstName, String lastName, String email, List<Profile> profiles) {
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
