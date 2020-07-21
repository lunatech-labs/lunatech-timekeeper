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

import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
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

@Path("/organizations")
public interface OrganizationResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all organizations",
            description = "Retrieve the list of organizations.")
    @Tag(ref = "organizations")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Organizations retrieved"
            )
    })
    List<OrganizationResponse> getAllOrganizations();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new organization ",
            description = "Create a organization.")
    @Tag(ref = "organizations")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Organization created",
                    headers = {
                            @Header(name = LOCATION, description = "New organization url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createOrganization(@RequestBody OrganizationRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an organization",
            description = "Retrieve organization details.")
    @Tag(ref = "organizations")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Organization retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Organization not found"
            )
    })
    OrganizationResponse getOrganization(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an organization",
            description = "Update organization details.")
    @Tag(ref = "organizations")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Organization updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Organization not found"
            )
    })
    Response updateOrganization(@PathParam("id") Long id, @RequestBody OrganizationRequest request);

}
