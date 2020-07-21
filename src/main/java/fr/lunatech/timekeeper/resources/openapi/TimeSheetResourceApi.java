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

import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/time-sheets")
public interface TimeSheetResourceApi {


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a time sheet",
            description = "Retrieve a time sheet details.")
    @Tag(ref = "timesheets")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "TimeSheet retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "TimeSheet not found"
            )
    })
    TimeSheetResponse getTimeSheet(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a time sheet",
            description = "Update time sheet details.")
    @Tag(ref = "timesheets")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "TimeSheet updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "TimeSheet not found"
            )
    })
    Response updateTimeSheet(@PathParam("id") Long id, @RequestBody TimeSheetRequest request);

}
