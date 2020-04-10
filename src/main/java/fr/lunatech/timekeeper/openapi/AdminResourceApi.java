package fr.lunatech.timekeeper.openapi;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/admin")
public interface AdminResourceApi {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Secure endpoint for testing", description = "Fake secure endpoint designed for simple test. A valid JWT token with Bearer: is required.")
    @Tag(ref = "security")
    @APIResponse(
            responseCode = "200",
            description = "a simple granted string"
    )
    String admin();
}
