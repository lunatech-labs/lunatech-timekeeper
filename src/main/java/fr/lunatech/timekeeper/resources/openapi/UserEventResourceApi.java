package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
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

@Path("/user-events")
public interface UserEventResourceApi {
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an user event",
            description = "Retrieve the user event by id")
    @Tag(ref = "events")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "EventTemplate retrieved"
            )
    })
    UserEventResponse getUserEventById(@PathParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create an user event",
            description = "Create a new userEvent.")
    @Tag(ref = "personnalEvents")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "UserEvent created",
                    headers = {
                            @Header(name = LOCATION, description = "New event url", schema = @Schema(type = SchemaType.STRING))
                    }
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad Request, an user event name with same start & end dates already exists."
            )
    })
    Response createUserEvent(@RequestBody UserEventRequest request, @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve user's userEvents",
            description = "Retrieve the list of existing userEvents for an user.")
    @Tag(ref = "personnalEvents")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "List of userEvents"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid userId value"),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token"),
            @APIResponse(
                    responseCode = "404",
                    description = "user not found"
            )
    })
    List<UserEventResponse> getPersonalEvents(@QueryParam("userId") Long id);

    @GET
    @Path("organization/{organizationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve user events by organization id",
            description = "Retrieve the list of existing userEvents by organization id.")
    @Tag(ref = "UserEventsByOrganizationId")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "List of user events"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid organizationId value"),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token"),
            @APIResponse(
                    responseCode = "404",
                    description = "user events not found"
            )
    })
    List<UserEventResponse> getEventsByOrganizationId(@PathParam("organizationId") Long organizationId);


}
