/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.EventTemplateResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.EventTemplateService;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class EventTemplateResource implements EventTemplateResourceApi {

    @Inject
    EventTemplateService eventTemplateService;

    @Inject
    AuthenticationContextProvider authentication;

    @Override
    public EventTemplateResponse getEventById(Long id) {
        return eventTemplateService.getById(id, authentication.context())
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    @Counted(name = "countGetAllEventsTemplate", description = "Counts how many times the user load the event list on method 'getAllEventsTemplate'")
    @Timed(name = "timeGetAllEventsTemplate", description = "Times how long it takes the user load the event list on method 'getAllEventsTemplate'", unit = MetricUnits.MILLISECONDS)
    public List<EventTemplateResponse> getAllEvents() {
        return eventTemplateService.getAllEventsTemplate(authentication.context());
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetEventUsers", description = "Counts how many times the user load the attendees for an event on method 'getEventUsers'")
    @Timed(name = "timeGetEventUsers", description = "Times how long it takes the user load the attendees for an event on method 'getEventUsers'", unit = MetricUnits.MILLISECONDS)
    public List<UserResponse> getEventUsers(Long eventId) {
        return eventTemplateService.getAttendees(eventId);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countCreateEvent", description = "Counts how many times the user create an event on method 'createEvent'")
    @Timed(name = "timeCreateEvent", description = "Times how long it takes the user create an event on method 'createEvent'", unit = MetricUnits.MILLISECONDS)
    public Response createEvent(@Valid EventTemplateRequest request, UriInfo uriInfo) {
        return eventTemplateService.create(request, authentication.context())
                .map(eventId ->
                        Response.created(
                                uriInfo.getAbsolutePathBuilder()
                                        .path(Long.toString(eventId))
                                        .build()
                        ).build()
                ).orElseThrow(() -> new IllegalEntityStateException(
                        "An event template with the same name, same start day and same end day already exists. Event name=[" + request.getName() +
                                "], start day date=" + request.getStartDateTime().toLocalDate() +
                                " end day date=" + request.getEndDateTime().toLocalDate())
                );
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countUpdateEvent", description = "Counts how many times the user to update an event on method 'updateEvent'")
    @Timed(name = "timeUpdateEvent", description = "Times how long it takes the user to update an event on method 'updateEvent'", unit = MetricUnits.MILLISECONDS)
    public Response updateEvent(Long eventTemplateId, EventTemplateRequest request) {
        Long updatedUserEvents = eventTemplateService.update(eventTemplateId, request, authentication.context());
        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("numberOfUpdatedUserEvents", String.format("%d", updatedUserEvents)) // e.getMessage can be null, but JSON format requires a value.
                        .build()
                        .toString()
                )
                .build();
    }
}
