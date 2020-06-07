package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.ProjectUser;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
    private final List<ProjectUserRequest> users;

    public ProjectRequest(
            @NotBlank String name,
            @NotNull Boolean billable,
            @NotNull String description,
            @Null Long clientId,
            @NotNull Boolean publicAccess,
            @NotNull List<ProjectUserRequest> users
    ) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientId = clientId;
        this.publicAccess = publicAccess;
        this.users = users;
    }

    public Project unbind(
            @NotNull Project project,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<Client>> findClient,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        project.organization = ctx.getOrganization();
        project.name = getName();
        project.billable = isBillable();
        project.description = getDescription();
        project.client = getClientId()
                .flatMap(clientId -> findClient.apply(clientId, ctx))
                .orElse(null);
        project.users = getUsers()
                .stream()
                .map(user -> user.unbind(project, findUser, ctx))
                .collect(Collectors.toList());
        project.publicAccess = isPublicAccess();
        return project;
    }

    public Project unbind(
            @NotNull BiFunction<Long, AuthenticationContext, Optional<Client>> findClient,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
            @NotNull AuthenticationContext ctx
    ) {
        return unbind(new Project(), findClient, findUser, ctx);
    }

    public Boolean notContains(@NotNull ProjectUser projectUser) {
        return getUsers()
                .stream()
                .noneMatch(o -> Objects.equals(projectUser.id, o.getId())) && projectUser.isPersistent();
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

    public Optional<Long> getClientId() {
        return Optional.ofNullable(clientId);
    }

    public Boolean isPublicAccess() {
        return publicAccess;
    }

    public List<ProjectUserRequest> getUsers() {
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

    /* 👤 ProjectUserRequest */
    public static final class ProjectUserRequest {

        @NotNull
        private final Long id;

        @NotNull
        private final Boolean manager;

        public ProjectUserRequest(
                @NotNull Long id,
                @NotNull Boolean manager
        ) {
            this.id = id;
            this.manager = manager;
        }

        public ProjectUser unbind(
                @NotNull Project project,
                @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findUser,
                @NotNull AuthenticationContext ctx
        ) {
            final var projectUser = project.getUser(getId()).orElse(new ProjectUser());
            projectUser.project = project;
            projectUser.manager = isManager();
            projectUser.user = findUser.apply(getId(), ctx)
                    .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown User. userId=%s", getId())));
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
