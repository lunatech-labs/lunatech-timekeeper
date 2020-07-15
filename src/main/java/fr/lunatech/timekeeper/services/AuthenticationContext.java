package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.models.time.EventTemplate;
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

    protected final boolean canCreate(@NotNull TimeEntry timeEntry) {
        final boolean canAccessTimeSheet = canAccess(timeEntry.timeSheet);
        final boolean isOwner = timeEntry.timeSheet.owner.id.equals(userId);
        return canAccessTimeSheet &&
                (isOwner ||
                        isAdmin() ||
                        isSuperAdmin());
    }

    protected final boolean canAccess(@NotNull TimeEntry timeEntry) {
        return canAccess(timeEntry.timeSheet.project.organization);
    }

    protected final boolean canAccess(@NotNull Organization organization) {
        return isSuperAdmin() || Objects.equals(getOrganization().id, organization.id);
    }

    protected final boolean canAccess(@NotNull Client client) {
        return canAccess(client.organization);
    }

    protected final boolean canAccess(@NotNull TimeSheet timeSheet) {
        return canAccess(timeSheet.project.organization);
    }

    protected final boolean canAccess(@NotNull User user) {
        return canAccess(user.organization);
    }

    protected final boolean canAccess(@NotNull EventTemplate eventTemplate) {
        return canAccess(eventTemplate.organization);
    }

    protected final boolean canAccess(@NotNull Project project) {
        if (!canAccess(project.organization)) {
            return false;
        } else if (isAdmin()) {
            return true;
        } else if (isSuperAdmin()) {
            return true;
        } else if (project.publicAccess) {
            return true;
        } else {
            if (project.users == null || project.users.isEmpty()) {
                return false;
            }
            return project.users.stream()
                    .filter(projectUser -> projectUser.user.id.equals(userId))
                    .findFirst()
                    .isPresent();
        }
    }

    protected final boolean canEdit(@NotNull Project project) {
        boolean organizationAccess = Objects.equals(getOrganization().id, project.organization.id);
        if (!organizationAccess) {
            return false;
        } else if (isAdmin() || isSuperAdmin()) {
            return true;
        } else {
            if (project.users == null || project.users.isEmpty()) {
                return false;
            }
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

    protected boolean canJoin(@NotNull Project project) {
        final boolean organizationAccess = Objects.equals(getOrganization().id, project.organization.id);
        return organizationAccess && project.publicAccess;
    }

    static AuthenticationContext bind(@NotNull User user) {
        return new AuthenticationContext(user.id, user.organization, user.profiles);
    }

    private Boolean isSuperAdmin() {
        return profiles.contains(Profile.SUPER_ADMIN);
    }

    private Boolean isAdmin() {
        return profiles.contains(Profile.ADMIN);
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
