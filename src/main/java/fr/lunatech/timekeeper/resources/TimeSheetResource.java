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
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TimeSheetResource implements TimeSheetResourceApi {

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;

    // TODO Do i really need this ? Remove it in service too if not | NOT NEEDED
    @RolesAllowed({"user", "admin"})
    @Override
    public Response createTimeSheet(TimeSheetRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeSheetId = timeSheetService.create(request,ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeSheetId)).build();
        return Response.created(uri).build();
    }

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
        timeSheetService.update(id, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
