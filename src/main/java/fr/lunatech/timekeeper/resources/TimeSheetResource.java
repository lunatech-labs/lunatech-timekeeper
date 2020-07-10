package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.TimeSheetResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

public class TimeSheetResource implements TimeSheetResourceApi {

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;


    @RolesAllowed({"user", "admin"})
    @Override
    public TimeSheetResponse getTimeSheet(Long id) {
        final var ctx = authentication.context();
        return timeSheetService.findTimeSheetById(id,ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateTimeSheet(Long id, TimeSheetRequest request) {
        final var ctx = authentication.context();
        return timeSheetService.update(id, request, ctx)
                .map(it ->  Response.noContent().build())
                .orElseThrow(NotFoundException::new);
    }
}
