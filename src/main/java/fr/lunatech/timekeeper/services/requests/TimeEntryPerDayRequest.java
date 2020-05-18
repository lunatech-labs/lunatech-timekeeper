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

public final class TimeEntryPerDayRequest implements TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final Long timeSheetId;

    @NotNull
    private final String date;


    @JsonbCreator
    public TimeEntryPerDayRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull Long timeSheetId,
            @NotNull String date
    ) {
        this.comment = comment;
        this.billable = billable;
        this.timeSheetId = timeSheetId;
        this.date = date;
    }

    public TimeEntry unbind(
            @NotNull Function<Long, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = TimeExtractor.extractStartTimeForDay(this.date);
        timeEntry.endDateTime = TimeExtractor.extractEndTimeForDay(this.date);
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

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "TimeEntryPerDayRequest{" +
                "comment='" + comment + '\'' +
                ", billable=" + billable +
                ", timeSheetId=" + timeSheetId +
                ", date='" + date +
                '}';
    }
}
