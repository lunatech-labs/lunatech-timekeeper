package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.responses.WeekResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/my")
public interface PersonalTimesheetsResourceApi {

    @GET
    @Path("/currentWeek")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve the current Week for you",
            description = "Retrieve the current week, with details about TimeSheets and Events")
    @Tag(ref = "personalTimeEntry")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Week successfully retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No current week for this user"),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token")
    })
    WeekResponse getCurrentWeek();

    @GET
    @Path("/currentMonth")
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
    List<WeekResponse> getCurrentMonth();

}
