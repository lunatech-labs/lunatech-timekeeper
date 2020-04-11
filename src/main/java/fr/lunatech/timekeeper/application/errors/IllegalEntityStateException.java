package fr.lunatech.timekeeper.application.errors;

import fr.lunatech.timekeeper.application.errors.BusinessException;

public class IllegalEntityStateException extends BusinessException {

    public IllegalEntityStateException(String message) {
        super(message);
    }

    public IllegalEntityStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
