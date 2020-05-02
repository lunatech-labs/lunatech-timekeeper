package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.ProjectUser;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public final class ProjectRequest {

    @NotBlank
    private final String name;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final String description;

    @Null
    private final Long clientId;

    @NotNull
    private final Boolean publicAccess;

    @NotNull
    private final List<User> users;

    @JsonbCreator
    public ProjectRequest(
            @NotBlank String name,
            @NotNull Boolean billable,
            @NotNull String description,
            @Null Long clientId,
            @NotNull Boolean publicAccess,
            @NotNull List<User> users
    ) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientId = clientId;
        this.publicAccess = publicAccess;
        this.users = users;
    }

    public Project unbind(
            Function<Long, Optional<fr.lunatech.timekeeper.models.Client>> retrieveClient,
            Function<Long, fr.lunatech.timekeeper.models.User> retrieveUser
    ) {
        return unbind(new Project(), retrieveClient, retrieveUser);
    }

    public Project unbind(
            fr.lunatech.timekeeper.models.Project project,
            Function<Long, Optional<fr.lunatech.timekeeper.models.Client>> retrieveClient,
            Function<Long, fr.lunatech.timekeeper.models.User> retrieveUser
    ) {
        project.name = name;
        project.billable = billable;
        project.description = description;
        project.client = ofNullable(clientId)
                .flatMap(retrieveClient)
                .orElse(null);
        project.users = users
                .stream()
                .map(user -> user.unbind(project, retrieveUser))
                .collect(Collectors.toList());
        project.publicAccess = publicAccess;
        return project;
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

    public Long getClientId() {
        return clientId;
    }

    public Boolean isPublicAccess() {
        return publicAccess;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "ProjectRequest{" +
                "name='" + name + '\'' +
                ", billable=" + billable +
                ", description='" + description + '\'' +
                ", clientId=" + clientId +
                ", publicAccess=" + publicAccess +
                ", users=" + users +
                '}';
    }

    /* 👤 ProjectRequest.User */
    public static final class User {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean manager;

        @JsonbCreator
        public User(
                @NotNull Long id,
                @NotNull Boolean manager
        ) {
            this.id = id;
            this.manager = manager;
        }

        public ProjectUser unbind(
                fr.lunatech.timekeeper.models.Project project,
                Function<Long, fr.lunatech.timekeeper.models.User> retrieveUser
        ) {
            final var projectUser = ofNullable(project.users)
                    .flatMap(projectUsers -> projectUsers
                            .stream()
                            .filter(pu -> pu.isSame(id, project.id))
                            .findFirst()
                    )
                    .orElse(new ProjectUser());

            projectUser.project = project;
            projectUser.user = retrieveUser.apply(id);
            projectUser.manager = manager;

            return projectUser;
        }

        public Long getId() {
            return id;
        }

        public Boolean isManager() {
            return manager;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", manager=" + manager +
                    '}';
        }
    }
}
