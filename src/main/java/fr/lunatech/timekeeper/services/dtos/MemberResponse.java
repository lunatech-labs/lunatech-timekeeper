package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Role;

import javax.validation.constraints.NotNull;

public final class MemberResponse {

    @NotNull
    private final Long id;
    @NotNull
    private final Long userId;
    @NotNull
    private final Role role;

    public MemberResponse(@NotNull Long id, @NotNull Long userId, @NotNull Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
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
}
