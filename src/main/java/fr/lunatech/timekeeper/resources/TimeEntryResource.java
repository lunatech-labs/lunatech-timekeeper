package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.TimeEntryResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeEntryService;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TimeEntryResource implements TimeEntryResourceApi {

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user"})
    @Override
    public Response createTimeEntry(@NotNull Long timeSheetId, @Valid TimeEntryRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(timeSheetId, request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response updateTimeEntry(Long timeSheetId, Long timeEntryId, @Valid TimeEntryRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        timeEntryService.updateTimeEntry(timeSheetId, timeEntryId, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
