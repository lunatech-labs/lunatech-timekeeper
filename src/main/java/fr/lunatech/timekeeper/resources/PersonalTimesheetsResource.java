package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.PersonalTimesheetsResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.MonthService;
import fr.lunatech.timekeeper.services.WeekService;
import fr.lunatech.timekeeper.services.responses.MonthResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * A Resource to serve only personal worksheet, timesheet and typical week for a specific user.
 * This facade hides the services behind.
 */
public class PersonalTimesheetsResource implements PersonalTimesheetsResourceApi {
    private static Logger logger = LoggerFactory.getLogger(PersonalTimesheetsResource.class);
    @Inject
    WeekService weekService;

    @Inject
    MonthService monthService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetPersonalWeek", description = "Counts how many times the user load his personal week on method 'getWeek'")
    @Timed(name = "timeGetPersonalWeek", description = "Times how long it takes the user load his personal week on method 'getWeek'", unit = MetricUnits.MILLISECONDS)
    public WeekResponse getWeek(Integer year, Integer weekNumber) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("getWeek year=%d weekNumber=%d", year, weekNumber));
        }
        final var ctx = authentication.context();
        return weekService.getWeek(ctx, year, weekNumber);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public MonthResponse getMonth(Integer year, Integer monthNumber) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("getMonth year=%d monthNumber=%d", year, monthNumber));
        }
        final var ctx = authentication.context();
        return monthService.getMonth(ctx, year, monthNumber);
    }

}
