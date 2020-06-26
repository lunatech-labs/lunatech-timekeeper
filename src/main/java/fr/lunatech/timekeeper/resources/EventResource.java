package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.EventResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.EventTemplateService;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class EventResource implements EventResourceApi {

    @Inject
    EventTemplateService eventTemplateService;

    @Inject
    AuthenticationContextProvider authentication;

    @Override
    public EventTemplateResponse getEventById(Long id) {
        return eventTemplateService.getById(id, authentication.context())
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public List<EventTemplateResponse> getAllEvents() {
        return eventTemplateService.listAllResponses(authentication.context());
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public List<UserResponse> getEventUsers(Long id) {
        return eventTemplateService.getAttendees(id);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response createEvent(@Valid EventTemplateRequest request, UriInfo uriInfo) {
        Long eventTemplateId = eventTemplateService.create(request, authentication.context());
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(eventTemplateId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateEvent(Long id, EventTemplateRequest request) {
        eventTemplateService.update(id, request,authentication.context())
                .orElseThrow(() -> new NotFoundException(String.format("Event Template not found for id=%d", id)));
        return Response.noContent().build();
    }
}
