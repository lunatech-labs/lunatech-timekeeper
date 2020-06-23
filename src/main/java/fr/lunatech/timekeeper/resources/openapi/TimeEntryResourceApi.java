package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.TimeEntryPerDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHalfDayRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerHourRequest;
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
    @Path("/day")
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
    Response createTimeEntryPerDay(@PathParam("timeSheetId") Long timeSheetId, @RequestBody TimeEntryPerDayRequest timeEntryRequest, @Context UriInfo uriInfo);

    @POST
    @Path("/hour")
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
    Response createTimeEntryPerHour(@PathParam("timeSheetId") Long timeSheetId, @RequestBody TimeEntryPerHourRequest timeEntryRequest, @Context UriInfo uriInfo);


    @POST
    @Path("/half-a-day")
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
    Response createTimeEntryPerHalfDay(@PathParam("timeSheetId") Long timeSheetId, @RequestBody TimeEntryPerHalfDayRequest timeEntryRequest, @Context UriInfo uriInfo);

    @PUT
    @Path("/{timeEntryId}/half-a-day")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an half day timeentry",
            description = "Update half day timeentry details.")
    @Tag(ref = "timeEntries")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "HalfDay TimeEntry updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "HalfDay TimeEntry not found"
            )
    })
    Response updateTimeEntryPerHalfDay(@PathParam("timeSheetId") Long timeSheetId, @PathParam("timeEntryId") Long timeEntryId, @Valid TimeEntryPerHalfDayRequest request, UriInfo uriInfo);

    @PUT
    @Path("/{timeEntryId}/hour")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an hour timeentry",
            description = "Update hour timeentry details.")
    @Tag(ref = "timeEntries")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Hour TimeEntry updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Hour TimeEntry not found"
            )
    })
    Response updateTimeEntryPerHour(@PathParam("timeSheetId") Long timeSheetId, @PathParam("timeEntryId") Long timeEntryId, @Valid TimeEntryPerHourRequest request, UriInfo uriInfo);

    @PUT
    @Path("/{timeEntryId}/day")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a day timeentry",
            description = "Update day timeentry details.")
    @Tag(ref = "timeEntries")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Day TimeEntry updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Day TimeEntry not found"
            )
    })
    Response updateTimeEntryPerDay(@PathParam("timeSheetId") Long timeSheetId, @PathParam("timeEntryId") Long timeEntryId, @Valid TimeEntryPerDayRequest request, UriInfo uriInfo);

}
