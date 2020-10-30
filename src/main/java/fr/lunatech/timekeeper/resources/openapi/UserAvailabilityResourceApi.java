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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/available")
public interface UserAvailabilityResourceApi {
    @GET
    @Path("/{start}/{end}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a collection of users",
            description = "Retrieve user availability.")
    @Tag(ref = "users")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Users retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No users found"
            )
    })
//    List<Map<String, List<Object>>> getAvailabilities(@PathParam("start") LocalDate start, @PathParam("end") LocalDate end);
    List<Map<String, List<Object>>> getAvailabilities(@PathParam("start") LocalDate start, @PathParam("end") LocalDate end);
    //TODO: To Map or to AvailabilityResponse
}
