package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
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
}