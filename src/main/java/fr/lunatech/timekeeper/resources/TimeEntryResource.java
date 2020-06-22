package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.TimeEntryResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeEntryService;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHalfDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHourRequest;

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
    public Response createTimeEntryPerDay(@NotNull Long timeSheetId, @Valid TimeEntryPerDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(timeSheetId, request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response createTimeEntryPerHour(@NotNull Long timeSheetId, @Valid TimeEntryPerHourRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(timeSheetId, request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response createTimeEntryPerHalfDay(@NotNull Long timeSheetId, @Valid TimeEntryPerHalfDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long timeId = timeEntryService.createTimeEntry(timeSheetId, request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response updateTimeEntryPerHalfDay(Long timeSheetId, Long timeEntryId, @Valid TimeEntryPerHalfDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        timeEntryService.updateTimeEntry(timeSheetId ,timeEntryId, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response updateTimeEntryPerHour(Long timeSheetId, Long timeEntryId, @Valid TimeEntryPerHourRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        timeEntryService.updateTimeEntry(timeSheetId, timeEntryId, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @RolesAllowed({"user"})
    @Override
    public Response updateTimeEntryPerDay(Long timeSheetId, Long timeEntryId, @Valid TimeEntryPerDayRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        timeEntryService.updateTimeEntry(timeSheetId, timeEntryId, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}