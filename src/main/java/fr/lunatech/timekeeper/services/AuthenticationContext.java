package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.TimeSheet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public final class AuthenticationContext {

    @NotNull
    private final Long userId;

    @NotEmpty
    private final List<Profile> profiles;

    @NotNull
    private final Organization organization;

    AuthenticationContext(
            @NotNull Long userId,
            @NotNull Organization organization,
            @NotEmpty List<Profile> profiles
    ) {
        this.userId = userId;
        this.organization = organization;
        this.profiles = profiles;
    }

    Boolean canAccess(@NotNull Client client) {
        return Objects.equals(getOrganization().id,  client.organization.id);
    }

    Boolean canAccess(@NotNull Organization organization) {
        return isSuperAdmin() || Objects.equals(getOrganization().id, organization.id);
    }

    Boolean canAccess(@NotNull Project project) {
        return Objects.equals(getOrganization().id, project.organization.id);
    }

    Boolean canAccess(@NotNull User user) {
        return Objects.equals(getOrganization().id, user.organization.id);
    }

    Boolean canAccess(@NotNull TimeSheet timesheet) {
        return Objects.equals(userId, timesheet.owner.id);
    }

    static AuthenticationContext bind(@NotNull User user) {
        return new AuthenticationContext(user.id, user.organization, user.profiles);
    }

    public Boolean isSuperAdmin() {
        return profiles.contains(Profile.SuperAdmin);
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
