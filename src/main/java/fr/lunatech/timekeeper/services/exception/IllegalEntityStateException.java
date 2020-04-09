package fr.lunatech.timekeeper.services.exception;

public class IllegalEntityStateException extends BusinessException {

    public IllegalEntityStateException(String message) {
        super(message);
    }

    public IllegalEntityStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
