package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.resources.exceptions.ResourceCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceCreationExceptionMapper implements ExceptionMapper<ResourceCreationException> {
    private static Logger logger = LoggerFactory.getLogger(ResourceCreationException.class);

    @Override
    public Response toResponse(ResourceCreationException e) {
        logger.warn(e.getMessage());
        // We could use also a CONFLICT but since we do not check the exact constraint name, we prefer to use a
        // more generic "Bad request" http response here.
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Json.createObjectBuilder()
                        .add("message", "" + e.getMessage())
                        .build())
                .build();
    }
}
