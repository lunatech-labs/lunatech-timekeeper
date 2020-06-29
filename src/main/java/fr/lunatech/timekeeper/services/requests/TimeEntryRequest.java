package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final LocalDateTime startDateTime;

    @NotNull
    private final Integer numberHours;

    public TimeEntryRequest(
            @NotBlank String comment,
            @NotNull LocalDateTime startDateTime,
            @NotNull Integer numberHours
    ) {
        this.comment = comment;
        this.startDateTime = startDateTime;
        this.numberHours = numberHours;
    }

    public TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        return unbind(timeEntry, timeSheetId, findTimeSheet, ctx);
    }

    public TimeEntry unbind(
            @NotNull TimeEntry timeEntry,
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        timeEntry.comment = getComment();
        timeEntry.startDateTime = getStartDateTime();
        timeEntry.endDateTime = getStartDateTime().plusHours(getNumberHours());
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId, ctx).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public Integer getNumberHours() {
        return numberHours;
    }

    @Override
    public String toString() {
        return "TimeEntryRequest{" +
                "comment='" + comment + '\'' +
                ", startDateTime=" + startDateTime +
                ", numberHours=" + numberHours +
                '}';
    }
}
