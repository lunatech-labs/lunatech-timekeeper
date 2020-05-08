package fr.lunatech.timekeeper.resources.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundResourceExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static Logger logger = LoggerFactory.getLogger(NotFoundResourceExceptionMapper.class);

    @Override
    public Response toResponse(NotFoundException e) {
        logger.warn(String.format("Resource not found message=%s", e.getMessage()));
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("message", e.getMessage())
                        .build())
                .build();
    }
}
