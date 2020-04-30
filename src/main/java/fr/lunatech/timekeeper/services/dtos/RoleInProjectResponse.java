package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Role;

import javax.validation.constraints.NotNull;

public final class RoleInProjectResponse {

    @NotNull
    private final Long id;
    @NotNull
    private final Long userId;
    @NotNull
    private final Role role;
    @NotNull
    private final Long projectId;

    public RoleInProjectResponse(@NotNull Long id, @NotNull Long userId, @NotNull Role role, Long projectId) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public Long getProjectId() {
        return projectId;
    }
}
