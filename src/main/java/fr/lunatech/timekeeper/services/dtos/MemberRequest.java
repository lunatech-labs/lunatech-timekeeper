package fr.lunatech.timekeeper.services.dtos;

import fr.lunatech.timekeeper.models.Role;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotNull;

public final class MemberRequest {

    @NotNull
    private final Long userId;
    @NotNull
    private final Role role;

    @JsonbCreator
    public MemberRequest(@NotNull Long userId, @NotNull Role role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "MemberRequest{" +
                "userId=" + userId +
                ", role=" + role +
                '}';
    }
}
