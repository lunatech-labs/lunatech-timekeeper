package fr.lunatech.timekeeper.resources.exceptions;

/**
 * This is a marker Exception to report that the Resource was not created.
 * Could be a duplicate or a validation reason.
 */
public class CreateResourceException extends RuntimeException {
    public CreateResourceException() {
        super();
    }

    public CreateResourceException(String message) {
        super(message);
    }

    public CreateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateResourceException(Throwable cause) {
        super(cause);
    }
}
