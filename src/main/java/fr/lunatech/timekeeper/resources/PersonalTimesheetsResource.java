package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.PersonalTimesheetsResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.WeekService;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * A Resource to serve only personal worksheet, timesheet and typical week for a specific user.
 * This facade hides the services behind.
 */
public class PersonalTimesheetsResource implements PersonalTimesheetsResourceApi {
    private static Logger logger = LoggerFactory.getLogger(PersonalTimesheetsResource.class);
    @Inject
    WeekService weekService;

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    public WeekResponse getWeek(Integer year, Integer weekNumber) {
        logger.debug(String.format("Get week for year=%d weekNumber=%d", year, weekNumber));
        final var ctx = authentication.context();
        return weekService.getWeek(ctx, year, weekNumber);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public List<WeekResponse> getMonth(Integer year, Integer monthNumber) {
        logger.info(String.format("getMonth year=%d monthNumber=%d NOT IMPLEMENTED", year, monthNumber));
        return Collections.emptyList();
    }

}
