package fr.lunatech.timekeeper.services.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class RoleInProjectUpdateRequest {

    @NotNull
    private final List<RoleInProjectRequest> rolesInProjects;

    @JsonbCreator
    public RoleInProjectUpdateRequest(@NotNull List<RoleInProjectRequest> rolesInProjects) {
        this.rolesInProjects = rolesInProjects;
    }

    public List<RoleInProjectRequest> getRolesInProjects() {
        return rolesInProjects;
    }

    @Override
    public String toString() {
        return "RoleInProjectUpdateRequest{" +
                "rolesInProjects=" + rolesInProjects +
                '}';
    }
}
