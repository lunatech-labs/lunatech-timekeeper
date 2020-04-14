package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.resources.JwtUser;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/users")
public interface UserResourceApi {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Extract current user from JWT",
            description = "Extract the current authenticated user from the JWT token. Endpoint required for the React application.")
    @Tag(ref = "security")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Bookings retrieved",
                    content = @Content(
                            schema = @Schema(
                                    implementation = JwtUser.class))
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Invalid JWT token")
    })
    JwtUser me();

}
