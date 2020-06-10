package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFR2020;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class WeekService {

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    /**
     * Loads a specific week for the current authenticated user
     *
     * @param ctx        is the context with the user that made this request
     * @param year       is a 4 digit Integer value for a year
     * @param weekNumber is an Integer value between 1 and 52
     * @return the WeekResponse with details for this user.
     */
    public WeekResponse getWeek(AuthenticationContext ctx, Integer year, Integer weekNumber) {
        if (year < 1000) {
            throw new IllegalEntityStateException("Invalid year value, value must be a 4 digits positive value");
        }
        if (weekNumber < 1 || weekNumber > 52) {
            throw new IllegalEntityStateException("Invalid weekNumber, value must be in range [1-52]");
        }

        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current week");
        }

        List<TimeSheetResponse> timeSheets = timeSheetService.findAllActivesForUser(ctx);

        var userEvents = new ArrayList<UserEvent>(); // TODO retrieve the specific user's events such as holidays
        var desiredDate = TimeKeeperDateUtils.retriveDateFromWeekNumber(weekNumber);
        var publicHolidays = new CalendarFR2020().getPublicHolidaysForWeekNumber(weekNumber);

        WeekResponse weekResponse = new WeekResponse(TimeKeeperDateUtils.adjustToFirstDayOfWeek(desiredDate)
                , userEvents
                , timeSheets
                , publicHolidays);

        return weekResponse;
    }


}
