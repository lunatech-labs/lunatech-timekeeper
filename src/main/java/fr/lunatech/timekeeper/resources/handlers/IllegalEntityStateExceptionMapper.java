package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalEntityStateExceptionMapper implements ExceptionMapper<IllegalEntityStateException> {

    private static Logger logger = LoggerFactory.getLogger(IllegalEntityStateExceptionMapper.class);

    @Override
    public Response toResponse(IllegalEntityStateException e) {
        logger.warn(String.format("IllegalEntityStateException %s ", e.getMessage()));
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("message", e.getMessage()).build()
                )
                .build();
    }
}
