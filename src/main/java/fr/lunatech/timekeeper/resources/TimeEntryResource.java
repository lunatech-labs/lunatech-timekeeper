package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.TimeEntryResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeEntryService;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHalfDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHourRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import fr.lunatech.timekeeper.timeutils.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
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
    public Response createTimeEntryPerDay(@Valid TimeEntryPerDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(request, ctx, TimeUnit.DAY);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response createTimeEntryPerHour(@Valid TimeEntryPerHourRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(request, ctx, TimeUnit.HOURLY);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response createTimeEntryPerHalfDay(@Valid TimeEntryPerHalfDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(request, ctx, TimeUnit.HALFDAY);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

}
