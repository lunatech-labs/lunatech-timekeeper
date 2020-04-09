package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.exception.IllegalEntityStateException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import java.util.Optional;

public abstract class HttpRespHandler {

    public <T> T notFoundHandler(Optional<T> value) {

        return value.orElseThrow(NotFoundException::new);

    }

    public <T> T statusCodeHandler(Process<T> process) {

        try {
            T value = process.run();
            return value;
        } catch (IllegalEntityStateException be) {
            throw new BadRequestException(be.getMessage());
            // TODO enhance businessException type to get more granularity in business error managment
        } catch (Exception err) {
            throw new ServerErrorException(err.getMessage(), Response.Status.INTERNAL_SERVER_ERROR, err.getCause());
        }


    }
}
