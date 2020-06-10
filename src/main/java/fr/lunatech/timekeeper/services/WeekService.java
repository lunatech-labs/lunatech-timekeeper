package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFR2020;

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
     * From the current date, returns the Week agenda of this user.
     *
     * @param ctx is the context with the current authenticated user
     * @return the WeekResponse object contains Public holidays, days off and TimeSheet(s)
     */
    public WeekResponse getCurrentWeek(AuthenticationContext ctx) {

        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current week");
        }

        List<TimeSheetResponse> timeSheets = timeSheetService.findAllActivesForUser(ctx);

        // 3. Load the list of User Events (like sick-leave or holidays)
        var userEvents = new ArrayList<UserEvent>();

        // Fake DATA - Temporary
        var nicolasDaysOff = new UserEvent();
        nicolasDaysOff.startDateTime = LocalDate.of(2020, 05, 22).atStartOfDay();
        nicolasDaysOff.endDateTime = LocalDate.of(2020, 05, 23).atStartOfDay();
        nicolasDaysOff.description = "Journée de congès";
        nicolasDaysOff.id = 1L;
        userEvents.add(nicolasDaysOff);

        var publicHolidays = new CalendarFR2020().getPublicHolidays();

        WeekResponse weekResponse = new WeekResponse(adjustToFirstDayOfWeek(LocalDate.now())
                , userEvents
                , timeSheets
                , publicHolidays);

        return weekResponse;
    }

    /**
     * Loads a specific week for the current authenticated user
     *
     * @param ctx        is the context with the user that made this request
     * @param weekNumber is an Integer value between 1 and 52
     * @return the WeekResponse with details for this user.
     */
    public WeekResponse getWeek(AuthenticationContext ctx, Integer weekNumber) {
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
        var publicHolidays = new CalendarFR2020().getPublicHolidays();
        var desiredDate = retriveDateFromWeekNumber(weekNumber);

        WeekResponse weekResponse = new WeekResponse(adjustToFirstDayOfWeek(desiredDate)
                , userEvents
                , timeSheets
                , publicHolidays);

        return weekResponse;
    }

    /**
     * Returns the LocalDate from a weekNumber, default is set to the Monday
     *
     * @param weekNumber is a value between 1 to 52
     * @return the Monday of this week number in the year
     */
    protected LocalDate retriveDateFromWeekNumber(final Integer weekNumber) {
        return LocalDate.now()
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Adjust a date to the first day of week, according to the current Locale.
     * For US Calendar, the week starts on Sunday. For European calendar, it is the Monday.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    protected LocalDate adjustToFirstDayOfWeek(LocalDate inputDate) {
        // We can use either the User Locale or the Organization Locale.
        // But for the time being I set it to FR so the first day of week is a Monday
        // This code will need to be updated later, to adjust user preferences and maybe relies on the AuthenticationContext
        TemporalField fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek();
        LocalDate firstDayOfWeek = inputDate.with(fieldISO, 1);
        return firstDayOfWeek;
    }
}
