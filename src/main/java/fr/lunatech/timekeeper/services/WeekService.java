package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
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
        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current week");
        }

        var userEvents = new ArrayList<UserEvent>();
        var publicHolidays = CalendarFactory.instanceFor("FR", year).getPublicHolidaysForWeekNumber(weekNumber);

        var startDayOfWeek = TimeKeeperDateUtils.getFirstDayOfWeekFromWeekNumber(year, weekNumber);
        final List<TimeSheetResponse> timeSheetsResponse = new ArrayList<>();
        for (TimeSheetResponse timeSheetResponse : timeSheetService.findAllActivesForUser(ctx)) {
            timeSheetsResponse.add(timeSheetResponse.filterTimeEntriesForWeek(startDayOfWeek));
        }

        return new WeekResponse(TimeKeeperDateUtils.adjustToFirstDayOfWeek(startDayOfWeek)
                , userEvents
                , timeSheetsResponse
                , publicHolidays);
    }


}
