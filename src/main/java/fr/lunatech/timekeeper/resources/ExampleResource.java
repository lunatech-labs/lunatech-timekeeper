package fr.lunatech.timekeeper.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

// Ce petit endpoint est pratique pour tester rapidement si Quarkus est bien démarré.
//TODO remplacer par microprofile - metrics
@Path("/api/hello")
public class ExampleResource {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);

    @Operation(summary = "Say hello to a TimeKeeper client")
    @APIResponse(responseCode = "200", content = @Content(mediaType = TEXT_PLAIN))
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        logger.debug("/hello called");
        return "Hello from TimeKeeper!";
    }
}
