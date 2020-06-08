package fr.lunatech.timekeeper.services.requests;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;


public final class TimeEntryPerDayRequest implements TimeEntryRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final Boolean billable;

    @NotNull
    private final LocalDate date;

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
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.billable = getBillable();
        timeEntry.comment = getComment();
        timeEntry.startDateTime = this.date.atStartOfDay().withHour(9).withMinute(0);
        timeEntry.endDateTime = this.date.atStartOfDay().withHour(17).withMinute(0);
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId, ctx).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
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
