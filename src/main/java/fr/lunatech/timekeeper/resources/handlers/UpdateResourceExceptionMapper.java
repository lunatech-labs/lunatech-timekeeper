package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler that should trigger a 400 Bad Request if a Resource was not updated.
 */
@Provider
public class UpdateResourceExceptionMapper implements ExceptionMapper<UpdateResourceException> {
    private static Logger logger = LoggerFactory.getLogger(UpdateResourceExceptionMapper.class);

    @Override
    public Response toResponse(UpdateResourceException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("message", String.format("%s", e.getMessage())) // e.getMessage can be null, but JSON format requires a value.
                        .build())
                .build();
    }
}
