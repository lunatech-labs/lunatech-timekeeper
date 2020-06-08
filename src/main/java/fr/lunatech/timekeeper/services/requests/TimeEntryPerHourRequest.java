package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;


public final class TimeEntryPerHourRequest implements TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final LocalDateTime startDateTime;

    @NotNull
    private final LocalDateTime endDateTime;

    public TimeEntryPerHourRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull LocalDateTime startDateTime,
            @NotNull LocalDateTime endDateTime
    ) {
        this.comment = comment;
        this.billable = billable;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = this.startDateTime;
        timeEntry.endDateTime = this.endDateTime;
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalEntityStateException("Start dateTime cannot be after endDateTime, invalid entry");
        }
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId, ctx).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public Boolean getBillable() {
        return billable;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return "TimeEntryRequest{" +
                "comment='" + comment + '\'' +
                ", billable=" + billable +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                '}';
    }
}
