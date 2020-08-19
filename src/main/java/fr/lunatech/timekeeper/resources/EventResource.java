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

import fr.lunatech.timekeeper.resources.openapi.EventResourceApi;
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
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
    @Counted(name = "countGetAllEvents", description = "Counts how many times the user load the event list on method 'getAllEvents'")
    @Timed(name = "timeGetAllEvents", description = "Times how long it takes the user load the event list on method 'getAllEvents'", unit = MetricUnits.MILLISECONDS)
    public List<EventTemplateResponse> getAllEvents() {
        return eventTemplateService.listAllResponses(authentication.context());
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetEventUsers", description = "Counts how many times the user load the attendees for an event on method 'getEventUsers'")
    @Timed(name = "timeGetEventUsers", description = "Times how long it takes the user load the attendees for an event on method 'getEventUsers'", unit = MetricUnits.MILLISECONDS)
    public List<UserResponse> getEventUsers(Long id) {
        return eventTemplateService.getAttendees(id);
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
                                "Event with name: " + request.getName() + ", " +
                                        "already exists with same Start: " + request.getStartDateTime().toLocalDate() + ", " +
                                        "and End: " + request.getEndDateTime().toLocalDate() +
                                        " dates."
                        )
                );
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countUpdateEvent", description = "Counts how many times the user to update an event on method 'updateEvent'")
    @Timed(name = "timeUpdateEvent", description = "Times how long it takes the user to update an event on method 'updateEvent'", unit = MetricUnits.MILLISECONDS)
    public Response updateEvent(Long id, EventTemplateRequest request) {
        return eventTemplateService.update(id, request, authentication.context())
                .map(it -> Response.noContent().build())
                .orElseThrow(() -> new NotFoundException(String.format("Event Template not found for id=%d", id)));
    }
}
