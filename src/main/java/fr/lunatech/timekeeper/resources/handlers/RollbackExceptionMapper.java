package fr.lunatech.timekeeper.resources.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.persistence.RollbackException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RollbackExceptionMapper implements ExceptionMapper<RollbackException> {
    private static Logger logger = LoggerFactory.getLogger(RollbackExceptionMapper.class);

    @Override
    public Response toResponse(RollbackException e) {
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
