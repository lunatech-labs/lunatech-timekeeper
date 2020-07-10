package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.responses.MonthResponse;
import fr.lunatech.timekeeper.services.responses.WeekResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/my")
public interface PersonalTimesheetsResourceApi {

    @GET
    @Path("/{year}/month")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve the current Month for you",
            description = "Retrieve the current mont, with details about TimeSheets and Events")
    @Tag(ref = "personalTimeEntry")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Month successfully retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No current Month for this user"),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token")
    })
    MonthResponse getMonth(@PathParam("year") Integer year,
                           @QueryParam("monthNumber") Integer monthNumber);

    @GET
    @Path("/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve the specified week of the year, for the current user token",
            description = "Retrieve a week in your agenda from the date, with details about TimeSheets and Events")
    @Tag(ref = "personalTimeEntry")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Week successfully retrieved"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid weeknumber, must be in the range 1 to 52"),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid year value"),
            @APIResponse(
                    responseCode = "404",
                    description = "No week for this user"),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token")
    })
    WeekResponse getWeek(@PathParam("year") Integer year,
                         @QueryParam("weekNumber") Integer weekNumber);

}
