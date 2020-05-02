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
    private final List<Project> projects;
    @NotNull
    private final Long organizationId;

    public UserResponse(
            @NotNull Long id,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email,
            @NotNull String picture,
            @NotEmpty List<Profile> profiles,
            @NotNull List<Project> projects,
            @NotNull Long organizationId
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.profiles = profiles;
        this.projects = projects;
        this.organizationId = organizationId;
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

    public List<Project> getProjects() {
        return projects;
    }

    public Long getOrganizationId() {
        return organizationId;
    }


    /* 🎁 UserResponse.Project */
    public static final class Project {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean manager;

        @NotNull
        private final String name;

        @NotNull
        private final Boolean publicAccess;

        public Project(
                @NotNull Long id,
                @NotNull Boolean manager,
                @NotNull String name,
                @NotNull Boolean publicAccess
        ) {
            this.id = id;
            this.manager = manager;
            this.name = name;
            this.publicAccess = publicAccess;
        }

        public Long getId() {
            return id;
        }

        public Boolean isManager() {
            return manager;
        }

        public String getName() {
            return name;
        }

        public Boolean isPublicAccess() {
            return publicAccess;
        }
    }
}
