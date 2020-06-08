package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
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

import static javax.ws.rs.core.HttpHeaders.LOCATION;

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
