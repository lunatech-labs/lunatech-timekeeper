package fr.lunatech.timekeeper.resources.handlers;

import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private static Logger logger = LoggerFactory.getLogger(ForbiddenExceptionMapper.class);

    @Override
    public Response toResponse(ForbiddenException e) {
        logger.warn(String.format("ForbiddenException %s ", e.getMessage()));
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(Json.createObjectBuilder()
                        .add("message", e.getMessage()).build()
                )
                .build();
    }
}
