package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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
        if (weekNumber < 1 || weekNumber > 53) {
            throw new IllegalEntityStateException("Invalid weekNumber, value must be in range [1-53]");
        }

        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current week");
        }

        List<TimeSheetResponse> timeSheets = timeSheetService.findAllActivesForUser(ctx);

//        // Fake DATA - Temporary - do not commit or it will break unit tests

//        // Fake DATA - start
//        var nicolasDaysOff = new UserEvent();
//        nicolasDaysOff.startDateTime= LocalDate.of(2020,05,22).atStartOfDay();
//        nicolasDaysOff.endDateTime=LocalDate.of(2020,05,23).atStartOfDay();
//        nicolasDaysOff.description="Journée de congès";
//        nicolasDaysOff.eventType="vacations";
//        nicolasDaysOff.id=1L;

        var userEvents = new ArrayList<UserEvent>();
//        userEvents.add(nicolasDaysOff);
//        // Fake DATA - END

        var desiredDate = TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(year, weekNumber);
        var publicHolidays = CalendarFactory.instanceFor("FR",year).getPublicHolidaysForWeekNumber(weekNumber);

        WeekResponse weekResponse = new WeekResponse(TimeKeeperDateUtils.adjustToFirstDayOfWeek(desiredDate)
                , userEvents
                , timeSheets
                , publicHolidays);

        return weekResponse;
    }


}
