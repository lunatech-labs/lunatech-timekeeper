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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.Function;

public final class TimeEntryPerDayRequest implements TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final LocalDate date;

    @JsonbCreator
    public TimeEntryPerDayRequest(
            @NotBlank String comment,
            @NotNull Boolean billable,
            @NotNull LocalDate date
    ) {
        this.comment = comment;
        this.billable = billable;
        this.date = date;
    }

    public TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull Function<Long, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = this.date.atStartOfDay();
        timeEntry.endDateTime = null ;
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public Boolean getBillable() {
        return billable;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "TimeEntryPerDayRequest{" +
                "comment='" + comment + '\'' +
                ", billable=" + billable +
                ", date='" + date +
                '}';
    }
}
