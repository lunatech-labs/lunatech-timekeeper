package fr.lunatech.timekeeper.services.dto;

import java.util.Objects;
import java.util.Optional;

public class UserTkDto {

    public UserTkDto() {
    }

    public UserTkDto(Optional<Long> id, String firstName, String lastname, String email, String profile) {
        this.id = id;
        this.firstName = firstName;
        this.lastname = lastname;
        this.email = email;
        this.profile = profile;
    }

    private Optional<Long> id = Optional.empty();
    private String firstName;
    private String lastname;
    private String email;
    private String profile;

    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTkDto userTkDto = (UserTkDto) o;
        return Objects.equals(id, userTkDto.id) &&
                Objects.equals(firstName, userTkDto.firstName) &&
                Objects.equals(lastname, userTkDto.lastname) &&
                Objects.equals(email, userTkDto.email) &&
                Objects.equals(profile, userTkDto.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastname, email, profile);
    }
}
