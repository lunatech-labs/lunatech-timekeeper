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
    @NotNull
    private final String picture;
    @NotEmpty
    private final List<Profile> profiles;
    @NotNull
    private final List<MemberResponse> memberOfprojects;

    public UserResponse(
            @NotNull Long id,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email,
            @NotNull String picture,
            @NotEmpty List<Profile> profiles,
            @NotNull List<MemberResponse> memberOfprojects
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.profiles = profiles;
        this.memberOfprojects = memberOfprojects;
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

    public String getPicture() {
        return picture;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public List<MemberResponse> getMemberOfprojects() {
        return memberOfprojects;
    }
}
