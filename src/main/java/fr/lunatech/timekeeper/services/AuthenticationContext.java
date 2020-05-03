package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public final class AuthenticationContext {

    @NotNull
    private final Long userId;

    @NotNull
    private final Organization organization;

    AuthenticationContext(@NotNull Long userId, @NotNull Organization organization) {
        this.userId = userId;
        this.organization = organization;
    }

    Boolean canAccess(@NotNull Client client) {
        return true; //TODO organization in Client
    }

    Boolean canAccess(@NotNull Organization organization) {
        return true; //TODO super admin or mine
    }

    Boolean canAccess(@NotNull Project project) {
        return Objects.equals(getOrganization().id, project.organization.id);
    }

    Boolean canAccess(@NotNull User user) {
        return Objects.equals(getOrganization().id, user.organization.id);
    }

    static AuthenticationContext bind(@NotNull User user) {
        return new AuthenticationContext(user.id, user.organization);
    }

    public Long getUserId() {
        return userId;
    }

    public Organization getOrganization() {
        return organization;
    }

    @Override
    public String toString() {
        return "AuthenticationContext{" +
                "userId=" + userId +
                ", organization=" + organization +
                '}';
    }
}
