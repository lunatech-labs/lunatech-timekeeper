package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.resources.exceptions.ConflictOnVersionException;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler that should trigger a 409 Conflict if there is an issue on the Version number
 */
@Provider
public class ConflictOnVersionExceptionMapper implements ExceptionMapper<ConflictOnVersionException> {
    private static Logger logger = LoggerFactory.getLogger(ConflictOnVersionExceptionMapper.class);

    @Override
    public Response toResponse(ConflictOnVersionException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("message", String.format("%s", e.getMessage())) // e.getMessage can be null, but JSON format requires a value.
                        .build())
                .build();
    }
}
