package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.services.ProjectServiceImpl;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalEntityStateExceptionMapper implements ExceptionMapper<IllegalEntityStateException> {

    private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);


    @Override
    public Response toResponse(IllegalEntityStateException e) {
        logger.warn("An illegal entity state is handled and transform to BAD_REQUEST (HTTP 400) Response", e);
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
