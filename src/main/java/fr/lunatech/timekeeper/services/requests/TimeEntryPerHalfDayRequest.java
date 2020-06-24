package fr.lunatech.timekeeper.services.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiFunction;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TimeEntryPerHalfDayRequest {

    @NotBlank
    private final String comment;

    @NotNull
    private final LocalDate date;

    @NotNull
    private final Boolean isMorning;

    public TimeEntryPerHalfDayRequest(
            @NotBlank String comment,

            @NotNull LocalDate date,
            @NotNull Boolean isMorning
    ) {
        this.comment = comment;
        this.date = date;
        this.isMorning = isMorning;
    }

    public TimeEntry unbind(
            @NotNull Long timeSheetId,
            @NotNull BiFunction<Long, AuthenticationContext, Optional<TimeSheet>> findTimeSheet,
            @NotNull AuthenticationContext ctx
    ) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.comment = getComment();
        if(isMorning){
            timeEntry.startDateTime =  this.date.atStartOfDay().plusHours(8);
            timeEntry.endDateTime = this.date.atStartOfDay().plusHours(12);
        }else{
            timeEntry.startDateTime = this.date.atStartOfDay().plusHours(13);
            timeEntry.endDateTime = this.date.atStartOfDay().plusHours(17);
        }
        timeEntry.timeSheet = findTimeSheet.apply(timeSheetId, ctx).orElseThrow(() -> new IllegalEntityStateException("TimeSheet not found for id " + timeSheetId));
        return timeEntry;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDate() {
        return date;
    }

    @JsonProperty(value="isMorning")
    public Boolean isMorning() {
        return isMorning;
    }

    @Override
    public String toString() {
        return "TimeEntryHalfADayRequest{" +
                "comment='" + comment + '\'' +
                ", date='" + date + '\'' +
                ", isMorning='" + isMorning + '\'' +
                '}';
    }
}
