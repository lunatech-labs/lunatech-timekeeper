package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.timeutils.TimeExtractor;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Optional;
import java.util.function.Function;

public final class TimeEntryPerHourRequest implements TimeEntryRequest{

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final Long timeSheetId;

    @NotNull
    private final String startDateTime;

    @NotNull
    private final String endDateTime;


    @JsonbCreator
    public TimeEntryPerHourRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull Long timeSheetId,
            @NotNull String startDateTime,
            @NotNull String endDateTime
    ) {
        this.comment = comment;
        this.billable = billable;
        this.timeSheetId = timeSheetId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public TimeEntry unbind(
            @NotNull Function<Long, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = TimeExtractor.extractStartTime(this.startDateTime);
        timeEntry.endDateTime = TimeExtractor.extractEndTime(this.endDateTime);
        timeEntry.timeSheet = findTimeSheet.apply(getTimeSheetId()).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + getTimeSheetId()));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public Boolean getBillable() {
        return billable;
    }

    public Long getTimeSheetId() {
        return timeSheetId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return "TimeEntryRequest{" +
                "comment='" + comment + '\'' +
                ", billable=" + billable +
                ", timeSheetId=" + timeSheetId +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                '}';
    }
}
