package fr.lunatech.timekeeper.resources.exceptions;

/**
 * This is a marker Exception to report that the Resource could not be updated.
 */
public class UpdateResourceException extends RuntimeException {
    public UpdateResourceException() {
        super();
    }

    public UpdateResourceException(String message) {
        super(message);
    }

    public UpdateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateResourceException(Throwable cause) {
        super(cause);
    }
}
