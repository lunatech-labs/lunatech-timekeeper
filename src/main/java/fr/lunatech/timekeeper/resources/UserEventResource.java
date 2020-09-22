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

import fr.lunatech.timekeeper.resources.openapi.UserEventResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.UserEventService;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
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

public class UserEventResource implements UserEventResourceApi {

    @Inject
    UserEventService userEventService;

    @Inject
    AuthenticationContextProvider authentication;

    @Override
    public UserEventResponse getUserEventById(Long id) {
        return userEventService.getUserEventById(id, authentication.context())
                .orElseThrow(NotFoundException::new);
    }


    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countgetPersonnalEvents", description = "Counts how many times the user load the event list on method 'getPersonnalEvents'")
    @Timed(name = "timegetPersonnalEvents", description = "Times how long it takes the user load the event list on method 'getPersonnalEvents'", unit = MetricUnits.MILLISECONDS)
    public List<UserEventResponse> getPersonnalEvents(Long userId) {
        return userEventService.listPersonnalEventForAnUser(userId, authentication.context());
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countCreateUserEvent", description = "Counts how many times the user create an event on method 'createUserEvent'")
    @Timed(name = "timeCreateUserEvent", description = "Times how long it takes the user create an event on method 'createUserEvent'", unit = MetricUnits.MILLISECONDS)
    public Response createUserEvent(@Valid UserEventRequest request, UriInfo uriInfo) {
        return userEventService.create(request, authentication.context())
                .map(eventId ->
                        Response.created(
                                uriInfo.getAbsolutePathBuilder()
                                        .path(Long.toString(eventId))
                                        .build()
                        ).build()
                ).orElseThrow(() -> new IllegalEntityStateException(
                        "An user event with the same name, same start day and same end day already exists. Event name=[" + request.getName() +
                                "], start day date=" + request.getStartDateTime().toLocalDate() +
                                " end day date=" + request.getEndDateTime().toLocalDate())
                );
    }
}
