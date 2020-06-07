package fr.lunatech.timekeeper.resources.handlers;

import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private static Logger logger = LoggerFactory.getLogger(ForbiddenExceptionMapper.class);

    @Override
    public Response toResponse(ForbiddenException e) {
        if (e.getMessage() == null) {
            logger.warn("ForbiddenException");
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Json.createObjectBuilder()
                            .add("message", "Access to this resource is forbidden for your role.").build()
                    )
                    .build();
        } else {
            logger.warn(String.format("ForbiddenException %s ", e.getMessage()));
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Json.createObjectBuilder()
                            .add("message", e.getMessage()).build()
                    )
                    .build();
        }
    }
}
