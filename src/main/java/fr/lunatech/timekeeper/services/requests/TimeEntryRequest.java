package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;

public class TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final LocalDateTime startDateTime;

    @NotNull
    private final Integer duration;

    public TimeEntryRequest(
            @NotBlank String comment,
            @NotNull LocalDateTime startDateTime,
            @NotNull Integer duration
    ) {
        this.comment = comment;
        this.startDateTime = startDateTime;
        this.duration = duration;
    }

    TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        return null;
    }

    TimeEntry unbind(
            @NotNull TimeEntry timeEntry,
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        return null;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public Integer getDuration() {
        return duration;
    }
}
