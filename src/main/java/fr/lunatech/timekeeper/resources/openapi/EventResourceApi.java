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

package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/events")
public interface EventResourceApi {


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an Event",
            description = "Retrieve the event details with all it's attendees")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Event retrieved"
            )
    })
    EventTemplateResponse getEventById(@PathParam("id") Long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all Events",
            description = "Retrieve the list of existing events for you organization")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Events retrieved"
            )
    })
    List<EventTemplateResponse> getAllEvents();

    @GET
    @Path("/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all attendees of an event",
            description = "Retrieve all attendees details concerned by the event")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "User list of the event retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    List<UserResponse> getEventUsers(@PathParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create an event",
            description = "Create a new event")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Event created",
                    headers = {
                            @Header(name = LOCATION, description = "New event url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createEvent(@RequestBody EventTemplateRequest request, @Context UriInfo uriInfo);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an event",
            description = "Update and event details")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Event updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    Response updateEvent(@PathParam("id") Long id, @RequestBody EventTemplateRequest request);


}
