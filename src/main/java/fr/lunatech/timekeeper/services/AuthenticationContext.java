package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        return Objects.equals(getOrganization().id, client.organization.id);
    }

    Boolean canAccess(@NotNull TimeSheet timeSheet) {
        return Objects.equals(getOrganization().id, timeSheet.project.organization.id);
    }

    Boolean canCreate(@NotNull TimeEntry timeEntry) {
        Boolean canAccessTimeSheet = canAccess(timeEntry.timeSheet);
        Boolean isOwner = timeEntry.timeSheet.owner.id.equals(userId);
        Boolean isAdmin = profiles.contains(Profile.Admin);
        return canAccessTimeSheet && (isOwner || isAdmin);
    }

    Boolean canAccess(@NotNull Organization organization) {
        return isSuperAdmin() || Objects.equals(getOrganization().id, organization.id);
    }

    Boolean canAccess(@NotNull Project project) {
        boolean organizationAccess = Objects.equals(getOrganization().id, project.organization.id);
        if (!organizationAccess) {
            return false;
        } else if (profiles.contains(Profile.Admin)) {
            return true;
        } else if (project.publicAccess) {
            return true;
        } else {
            Optional<ProjectUser> currentProjectUser = project.users.stream()
                    .filter(projectUser -> projectUser.user.id.equals(userId))
                    .findFirst();
            if (currentProjectUser.isEmpty()) {
                return false;
            } else {
                return currentProjectUser.get().manager;
            }
        }
    }

    Boolean canAccess(@NotNull TimeEntry timeEntry) {
        return Objects.equals(getOrganization().id, timeEntry.timeSheet.project.organization.id);
    }

    Boolean canEdit(@NotNull Project project) {
        boolean organizationAccess = Objects.equals(getOrganization().id, project.organization.id);
        if (!organizationAccess) {
            return false;
        } else if (profiles.contains(Profile.Admin)) {
            return true;
        } else {
            Optional<ProjectUser> currentProjectUser = project.users.stream()
                    .filter(projectUser -> projectUser.user.id.equals(userId))
                    .findFirst();
            if (currentProjectUser.isEmpty()) {
                return false;
            } else {
                return currentProjectUser.get().manager;
            }
        }
    }

    Boolean canJoin(@NotNull Project project) {
        boolean organizationAccess = Objects.equals(getOrganization().id, project.organization.id);
        return organizationAccess && project.publicAccess;
    }

    Boolean canAccess(@NotNull User user) {
        return Objects.equals(getOrganization().id, user.organization.id);
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
