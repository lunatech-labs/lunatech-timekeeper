package fr.lunatech.timekeeper.services.responses.project;

import fr.lunatech.timekeeper.models.ProjectUser;

import javax.validation.constraints.NotNull;

public final class ProjectUserResponse {

    @NotNull
    private final Long id;

    @NotNull
    private final Boolean manager;

    @NotNull
    private final String name;

    @NotNull
    private final String picture;

    public ProjectUserResponse(
            @NotNull Long id,
            @NotNull Boolean manager,
            @NotNull String name,
            @NotNull String picture
    ) {
        this.id = id;
        this.manager = manager;
        this.name = name;
        this.picture = picture;
    }

    public static ProjectUserResponse bind(@NotNull ProjectUser projectUser) {
        return new ProjectUserResponse(
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

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}