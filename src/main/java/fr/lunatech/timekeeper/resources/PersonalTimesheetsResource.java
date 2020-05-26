package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.resources.openapi.PersonalTimesheetsResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.WeekService;
import fr.lunatech.timekeeper.services.responses.WeekResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
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
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user"})
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
