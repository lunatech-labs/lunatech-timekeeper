package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.Week;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public final class WeekResponse {

    private final LocalDate firstDayOfWeek;

    @NotNull
    private final List<UserEvent> userEvents;

    private final List<TimeSheetResponse> sheets;

    public WeekResponse(
            @NotBlank LocalDate firstDayOfWeek,
            @NotNull List<UserEvent> userEvents,
            @NotNull List<TimeSheetResponse> sheets
    ) {
        this.firstDayOfWeek = firstDayOfWeek;
        this.userEvents = userEvents;
        this.sheets = sheets;
    }

    public static WeekResponse bind(@NotNull Week week, List<TimeSheet> sheets) {
        return new WeekResponse(
                week.firstDayOfWeek,
                week.userEvents,
                sheets.stream()
                .map(TimeSheetResponse::bind)
                .collect(Collectors.toList())
        );
    }

    public LocalDate getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    public List<TimeSheetResponse> getSheets() {
        return sheets;
    }
}
