package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CreateResourceExceptionMapper implements ExceptionMapper<CreateResourceException> {
    private static Logger logger = LoggerFactory.getLogger(CreateResourceExceptionMapper.class);

    @Override
    public Response toResponse(CreateResourceException e) {
        logger.warn(e.getMessage());
        // We could use also a CONFLICT but since we do not check the exact constraint name, we prefer to use a
        // more generic "Bad request" http response here.
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Json.createObjectBuilder()
                        .add("message", String.format("%s", e.getMessage())) // e.getMessage can be null, but JSON format requires a value.
                        .build())
                .build();
    }
}
