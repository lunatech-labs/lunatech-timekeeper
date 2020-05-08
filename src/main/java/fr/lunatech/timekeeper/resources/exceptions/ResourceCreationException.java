package fr.lunatech.timekeeper.resources.exceptions;

/**
 * This is a marker Exception to report that the Resource was not created.
 * Could be a duplicate or a validation reason.
 */
public class ResourceCreationException extends RuntimeException {
    public ResourceCreationException() {
        super();
    }

    public ResourceCreationException(String message) {
        super(message);
    }

    public ResourceCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceCreationException(Throwable cause) {
        super(cause);
    }
}
