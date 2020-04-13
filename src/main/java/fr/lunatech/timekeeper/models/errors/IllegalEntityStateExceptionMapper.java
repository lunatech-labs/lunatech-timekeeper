package fr.lunatech.timekeeper.models.errors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalEntityStateExceptionMapper implements ExceptionMapper<IllegalEntityStateException> {

    @Override
    public Response toResponse(IllegalEntityStateException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
