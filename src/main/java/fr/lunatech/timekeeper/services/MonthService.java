package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.MonthResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MonthService {

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    public MonthResponse getMonth(AuthenticationContext ctx, Integer year, Integer monthNumber) {
        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if (maybeUser.isEmpty()) {
            throw new IllegalStateException("User not found, cannot load current month");
        }

        var userEvents = new ArrayList<UserEvent>();
        if (monthNumber == null) {
            monthNumber = LocalDate.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        var publicHolidays = CalendarFactory.instanceFor("FR", year).getPublicHolidaysForMonthNumber(monthNumber);

        final List<TimeSheetResponse> timeSheetResponseList = new ArrayList<>();
        for (TimeSheetResponse timeSheetResponse : timeSheetService.findAllActivesForUser(ctx)) {
            timeSheetResponseList.add(timeSheetResponse.filterToSixWeeksRange(year, monthNumber));
        }

        return new MonthResponse(userEvents
                , timeSheetResponseList
                , publicHolidays);

    }
}
