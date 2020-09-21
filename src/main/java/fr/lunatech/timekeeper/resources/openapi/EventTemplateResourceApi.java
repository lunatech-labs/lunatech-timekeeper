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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/events-template")
public interface EventTemplateResourceApi {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an eventTemplate",
            description = "Retrieve the eventTemplate's details")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "EventTemplate retrieved"
            )
    })
    EventTemplateResponse getEventById(@PathParam("id") Long id);

    @GET
    @RolesAllowed({"admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all eventTemplates",
            description = "Retrieve the list of existing eventTemplates for you organization. Each event also returns the list of current attendees or participants.")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "List of eventTemplates"
            )
    })
    List<EventTemplateResponse> getAllEvents();

    @GET
    @Path("/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all attendees of an eventTemplate",
            description = "Retrieve all attendees details concerned by the eventTemplate")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "All userEvents related to this eventTemplate"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "eventTemplate not found"
            )
    })
    List<UserResponse> getEventUsers(@PathParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create an eventTemplate",
            description = "Create a new eventTemplate and create associated userEvent for each eventTemplateRequest attendee.")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "EventTemplate created",
                    headers = {
                            @Header(name = LOCATION, description = "New event url", schema = @Schema(type = SchemaType.STRING))
                    }
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad Request, an eventTemplate name with same start & end dates already exists."
            )
    })
    Response createEvent(@RequestBody EventTemplateRequest request, @Context UriInfo uriInfo);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an event and the list of attendees",
               description = "Update an eventTemplate, this operation will also flush and recreate all associated UserEvents for each attendee defined in the EventTemplateRequest.")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Returns the number of associated userEvents updated or created"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "EventTemplate not found"
            )
    })
    Response updateEvent(@PathParam("id") Long eventTemplateId, @RequestBody EventTemplateRequest request);


}
