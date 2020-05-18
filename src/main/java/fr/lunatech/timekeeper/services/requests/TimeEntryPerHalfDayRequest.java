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

public final class TimeEntryPerHalfDayRequest implements TimeEntryRequest{

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final Long timeSheetId;

    @NotNull
    private final String date;

    @NotNull
    private final Boolean isMorning;


    @JsonbCreator
    public TimeEntryPerHalfDayRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull Long timeSheetId,
            @NotNull String date,
            @NotNull Boolean isMorning
    ) {
        this.comment = comment;
        this.billable = billable;
        this.timeSheetId = timeSheetId;
        this.date = date;
        this.isMorning = isMorning;
    }

    public TimeEntry unbind(
            @NotNull Function<Long, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = TimeExtractor.extractStartTime(this.date, this.isMorning);
        timeEntry.endDateTime = TimeExtractor.extractEndTime(this.date, this.isMorning);
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

    public Boolean isMorning() {
        return isMorning;
    }

    @Override
    public String toString() {
        return "TimeEntryHalfADayRequest{" +
                "comment='" + comment + '\'' +
                ", billable=" + billable +
                ", timeSheetId=" + timeSheetId +
                ", date='" + date + '\'' +
                ", isMorning='" + isMorning + '\'' +
                '}';
    }
}
