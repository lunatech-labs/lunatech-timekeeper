package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public final class ProjectResponse {

    @NotNull
    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final String description;

    @Null
    private final Client client;

    @NotNull
    private final List<User> users;

    @NotNull
    private final Boolean publicAccess;

    public ProjectResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull Boolean billable,
            @NotNull String description,
            @Null Client client,
            @NotNull List<User> users,
            @NotNull Boolean publicAccess
    ) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.client = client;
        this.users = users;
        this.publicAccess = publicAccess;
    }

    public static ProjectResponse bind(Project project) {
        return new ProjectResponse(
                project.id,
                project.name,
                project.billable,
                project.description,
                ofNullable(project.client)
                        .map(ProjectResponse.Client::bind)
                        .orElse(null),
                project.users
                        .stream()
                        .map(ProjectResponse.User::bind)
                        .collect(Collectors.toList()),
                project.publicAccess
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean isBillable() {
        return billable;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Client> getClient() {
        return Optional.ofNullable(client);
    }

    public List<User> getUsers() {
        return users;
    }

    public Boolean isPublicAccess() {
        return publicAccess;
    }


    /* 👤 ProjectResponse.User */
    public static final class User {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean manager;

        @NotNull
        private final String fullName;

        @NotNull
        private final String picture;

        public User(
                @NotNull Long id,
                @NotNull Boolean manager,
                @NotNull String fullName,
                @NotNull String picture
        ) {
            this.id = id;
            this.manager = manager;
            this.fullName = fullName;
            this.picture = picture;
        }

        public static ProjectResponse.User bind(fr.lunatech.timekeeper.models.ProjectUser projectUser) {
            return new ProjectResponse.User(
                    projectUser.user.id,
                    projectUser.manager,
                    projectUser.user.getFullName(),
                    projectUser.user.picture
            );
        }

        public Long getId() {
            return id;
        }

        public Boolean getManager() {
            return manager;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPicture() {
            return picture;
        }
    }

    /* 🌐 ProjectResponse.Client */
    public static final class Client {

        @NotNull
        private final Long id;

        @NotNull
        private final String name;

        public Client(
                @NotNull Long id,
                @NotNull String name
        ) {
            this.id = id;
            this.name = name;
        }

        public static ProjectResponse.Client bind(fr.lunatech.timekeeper.models.Client client) {
            return new ProjectResponse.Client(
                    client.id,
                    client.name
            );
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
