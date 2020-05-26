package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.timeutils.Calendar;
import fr.lunatech.timekeeper.timeutils.CalendarFR2020;
import fr.lunatech.timekeeper.timeutils.PublicHoliday;
import fr.lunatech.timekeeper.timeutils.Week;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public final class WeekResponse {

    private final LocalDate firstDayOfWeek;

    private final List<UserEvent> userEvents;

    private final List<TimeSheetResponse> sheets;

    private final List<PublicHoliday> publicHolidays;

    public WeekResponse(
            @NotBlank LocalDate firstDayOfWeek,
            @NotNull List<UserEvent> userEvents,
            @NotNull List<TimeSheetResponse> sheets,
            @NotNull List<PublicHoliday> publicHolidays
    ) {
        this.firstDayOfWeek = firstDayOfWeek;
        this.userEvents = userEvents;
        this.sheets = sheets;
        this.publicHolidays=publicHolidays;
    }

    public static WeekResponse bind(@NotNull Week week, List<TimeSheet> sheets, List<PublicHoliday> publicHolidays) {
        return new WeekResponse(
                week.firstDayOfWeek,
                week.userEvents,
                sheets.stream()
                .map(TimeSheetResponse::bind)
                .collect(Collectors.toList()),
                publicHolidays
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
