package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.PersonalTimesheetsResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.WeekService;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * A Resource to serve only personal worksheet, timesheet and typical week for a specific user.
 * This facade hides the services behind.
 */
public class PersonalTimesheetsResource implements PersonalTimesheetsResourceApi {

    @Inject
    WeekService weekService;

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    public List<TimeSheetResponse> getAllTimeSheet() {
        final var ctx = authentication.context();
        return timeSheetService.findAll(ctx);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public WeekResponse getCurrentWeek() {
        final var ctx = authentication.context();
        return weekService.getCurrentWeek(ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user"})
    @Override
    public List<WeekResponse> getCurrentMonth() {
        // Recupere les TimeSheets actives
        return Collections.emptyList();
    }

}
