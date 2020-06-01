package fr.lunatech.timekeeper.services.responses.project;

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
    private final ProjectClientResponse client;

    @NotNull
    private final List<ProjectUserResponse> users;

    @NotNull
    private final Boolean publicAccess;

    public ProjectResponse(
            @NotNull Long id,
            @NotBlank String name,
            @NotNull Boolean billable,
            @NotNull String description,
            @Null ProjectClientResponse client,
            @NotNull List<ProjectUserResponse> users,
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

    public static ProjectResponse bind(@NotNull Project project) {
        return new ProjectResponse(
                project.id,
                project.name,
                project.billable,
                project.description,
                ofNullable(project.client)
                        .map(ProjectClientResponse::bind)
                        .orElse(null),
                project.users
                        .stream()
                        .map(ProjectUserResponse::bind)
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

    public Optional<ProjectClientResponse> getClient() {
        return Optional.ofNullable(client);
    }

    public List<ProjectUserResponse> getUsers() {
        return users;
    }

    public Boolean isPublicAccess() {
        return publicAccess;
    }
}
