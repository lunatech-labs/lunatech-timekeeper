package fr.lunatech.timekeeper.dtos;

import fr.lunatech.timekeeper.models.Role;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotNull;

public final class MemberRoleRequest {

    @NotNull
    private final Role role;

    @JsonbCreator
    public MemberRoleRequest(@NotNull Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
