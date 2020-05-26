package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFR2020;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeekService {
    private static Logger logger = LoggerFactory.getLogger(WeekService.class);

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    public Optional<WeekResponse> getCurrentWeek(AuthenticationContext ctx) {

        Long userId = ctx.getUserId();
        Optional<User> maybeUser = userService.findById(userId, ctx);
        if(maybeUser.isEmpty()){
            throw new IllegalStateException("User not found, cannot load current week");
        }

        List<TimeSheetResponse> timeSheets =timeSheetService.findAllActivesForUser(ctx);

        // TODO
        logger.warn("TODO getCurrentWeek");

        // 3. Load the list of User Events (like sick-leave or holidays)
        var userEvents = new ArrayList<UserEvent>();

        // Fake DATA - Temporary
        var nicolasDaysOff = new UserEvent();
        nicolasDaysOff.startDateTime=LocalDate.of(2020,05,22).atStartOfDay();
        nicolasDaysOff.endDateTime=LocalDate.of(2020,05,23).atStartOfDay();
        nicolasDaysOff.description="Journée de congès";
        nicolasDaysOff.id=1L;


        var publicHolidays = new CalendarFR2020().getPublicHolidays();

        // 4. Create the structure and return the result
        // Fake DATA - Temporary
        WeekResponse weekResponse = new WeekResponse(LocalDate.of(2020,05,18)
        , userEvents
        , timeSheets
        , publicHolidays);


        return Optional.ofNullable(weekResponse);

    }
}
