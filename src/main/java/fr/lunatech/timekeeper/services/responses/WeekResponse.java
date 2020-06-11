package fr.lunatech.timekeeper.services.responses;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.PublicHoliday;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import fr.lunatech.timekeeper.timeutils.Week;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class WeekResponse {

    private final LocalDate firstDayOfWeek;

    private final List<UserEventResponse> userEvents;

    private final List<TimeSheetResponse> sheets;

    private final List<PublicHoliday> publicHolidays;

    public WeekResponse(
            @NotBlank LocalDate firstDayOfWeek,
            @NotNull List<UserEvent> userEvents,
            @NotNull List<TimeSheetResponse> sheets,
            @NotNull List<PublicHoliday> publicHolidays
    ) {
        this.firstDayOfWeek = firstDayOfWeek;
        this.userEvents = userEvents
                .stream()
                .map( userEvent -> UserEventResponse.bind(userEvent))
                .collect(Collectors.toList());
        this.sheets = sheets;
        this.publicHolidays = publicHolidays;
    }

    public static WeekResponse bind(@NotNull Week week,
                                    List<TimeSheet> sheets,
                                    List<PublicHoliday> publicHolidays) {
        return new WeekResponse(
                week.firstDayOfWeek,
                week.userEvents,
                sheets.stream()
                        .map(TimeSheetResponse::bind)
                        .collect(Collectors.toList()),
                publicHolidays
        );
    }

    public String getFirstDayOfWeek() {
        if (firstDayOfWeek == null) return null;
        return TimeKeeperDateUtils.formatToString(firstDayOfWeek);
    }

    public Integer getWeekNumber() {
        if (firstDayOfWeek == null) return null;
        return TimeKeeperDateUtils.getWeekNumberFromDate(firstDayOfWeek);
    }

    public List<UserEventResponse> getUserEvents() {
        return Collections.unmodifiableList(userEvents);
    }

    public List<TimeSheetResponse> getSheets() {
        return Collections.unmodifiableList(sheets);
    }

    public List<PublicHoliday> getPublicHolidays() {
        return Collections.unmodifiableList(publicHolidays);
    }
}
