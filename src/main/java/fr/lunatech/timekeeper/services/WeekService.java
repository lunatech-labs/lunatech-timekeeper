package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import fr.lunatech.timekeeper.timeutils.CalendarFR2020;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeekService {
    private static Logger logger = LoggerFactory.getLogger(WeekService.class);

    public Optional<WeekResponse> getCurrentWeek(Long userId) {

        // TODO
        logger.warn("TODO getCurrentWeek");

        var userEvents = new ArrayList<UserEvent>();

        // Fake DATA - Temporary
        var nicolasDaysOff = new UserEvent();
        nicolasDaysOff.startDateTime=LocalDate.of(2020,05,22).atStartOfDay();
        nicolasDaysOff.endDateTime=LocalDate.of(2020,05,23).atStartOfDay();
        nicolasDaysOff.description="Journée de congès";
        nicolasDaysOff.id=1L;

        // Fake DATA - Temporary
        var sheets = new ArrayList<TimeSheetResponse>();

        var publicHolidays = new CalendarFR2020().getPublicHolidays();

        // Fake DATA - Temporary
        WeekResponse weekResponse = new WeekResponse(LocalDate.of(2020,05,18)
        , userEvents
        , sheets
        , publicHolidays);


        return Optional.ofNullable(weekResponse);

    }
}
