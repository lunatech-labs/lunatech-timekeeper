package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.timeutils.TimeUnit;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;

public class TimeSheetRequest {
    @NotNull
    public Long projectId;

    @NotNull
    public Long ownerId;

    @Enumerated(EnumType.STRING)
    public TimeUnit timeUnit;

    public Boolean defaultIsBillable;

    // 1---------------
    @Null
    public LocalDate expirationDate;

    // 2 ------------------
    @Null
    public Integer maxDuration; // eg 21

    @Null
    public TimeUnit durationUnit; // DAYS

    public TimeSheetRequest(@NotNull Long projectId, @NotNull Long ownerId, TimeUnit timeUnit, Boolean defaultIsBillable, @Null LocalDate expirationDate, @Null Integer maxDuration, @Null TimeUnit durationUnit) {
        this.projectId = projectId;
        this.ownerId = ownerId;
        this.timeUnit = timeUnit;
        this.defaultIsBillable = defaultIsBillable;
        this.expirationDate = expirationDate;
        this.maxDuration = maxDuration;
        this.durationUnit = durationUnit;
    }

    public TimeSheet unbind(
            @NotNull BiFunction<Long, AuthenticationContext, Optional<Project>> findProject,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<User>> findOwner,
            @NotNull AuthenticationContext ctx
    ) {
        TimeSheet timeSheet = new TimeSheet();
        timeSheet.project = findProject.apply(getProjectId(), ctx).orElseThrow(() -> new IllegalEntityStateException("Project not found for id " + getProjectId()));
        timeSheet.owner = findOwner.apply(getOwnerId(), ctx).orElseThrow(() -> new IllegalEntityStateException("Owner not found for id " + getProjectId()));
        timeSheet.timeUnit = getTimeUnit();
        timeSheet.defaultIsBillable = getDefaultIsBillable();
        timeSheet.expirationDate = getExpirationDate();
        timeSheet.maxDuration = getMaxDuration();
        timeSheet.durationUnit = getDurationUnit();
        return timeSheet;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Boolean getDefaultIsBillable() {
        return defaultIsBillable;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }
}
