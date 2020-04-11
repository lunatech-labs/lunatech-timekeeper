package fr.lunatech.timekeeper.application.errors;

public class IllegalEntityStateException extends BusinessException {

    public IllegalEntityStateException(String message) {
        super(message);
    }

    public IllegalEntityStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
