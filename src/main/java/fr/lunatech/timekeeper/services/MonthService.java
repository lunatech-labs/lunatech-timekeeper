package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.MonthResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFactory;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        var publicHolidays = CalendarFactory.instanceFor("FR", year).getPublicHolidaysForMonthNumber(monthNumber);

        Predicate<LocalDate> isValidDate = TimeKeeperDateUtils.isIncludedInSixWeeksFromMonth(year, monthNumber);
        final List<TimeSheetResponse> timeSheetsResponse = timeSheetService
                .findAllActivesForUser(ctx)
                .stream().peek(timeSheetResponse ->
                        timeSheetResponse.entries = timeSheetResponse.entries
                                .stream()
                                .filter(timeEntryResponse -> {
                                    LocalDateTime startDateTime = timeEntryResponse.getStartDateTime();
                                    return isValidDate.test(startDateTime.toLocalDate());
                                })
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return new MonthResponse(userEvents
                , timeSheetsResponse
                , publicHolidays);

    }
}
