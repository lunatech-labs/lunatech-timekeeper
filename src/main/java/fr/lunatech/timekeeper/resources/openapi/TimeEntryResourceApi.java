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

import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/timeSheet/{timeSheetId}/timeEntry")
public interface TimeEntryResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new TimeEntry for a Project",
            description = "Create a TimeEntry linked to a Project and a User, with a duration and a startTime")
    @Tag(ref = "timeEntries")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Time entry created",
                    headers = {
                            @Header(name = LOCATION,
                                    description = "New time entry URI",
                                    schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createTimeEntry(@PathParam("timeSheetId") Long timeSheetId, @RequestBody TimeEntryRequest timeEntryRequest, @Context UriInfo uriInfo);

    @PUT
    @Path("/{timeEntryid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a timeentry",
            description = "Update timeentry details.")
    @Tag(ref = "timeEntries")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "TimeEntry updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "TimeEntry not found"
            )
    })
    Response updateTimeEntry(@PathParam("timeSheetId") Long timeSheetId, @PathParam("timeEntryid") Long timeEntryId, @Valid @RequestBody TimeEntryRequest request, @Context UriInfo uriInfo);

//    @Gauge(name = "numberOfHalfDayEntries", unit = MetricUnits.NONE, description = "Number of entries created for a half day")
    Integer numberOfHalfDayEntries();

//    @Gauge(name = "numberOf8HoursEntries", unit = MetricUnits.NONE, description = "Number of entries created for a full day")
    Integer numberOfDayEntries();

//    @Gauge(name = "numberOfOtherHoursEntries", unit = MetricUnits.NONE, description = "Number of entries created with an amount of hours different than 1, 4 and 8 hours")
    Integer numberOfOtherHoursEntries();

    @Path("/yolo")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces("text/plain")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "TimeEntry updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "TimeEntry not found"
            )
    })
    Response yolo();
}