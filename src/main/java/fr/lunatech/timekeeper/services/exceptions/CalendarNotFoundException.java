package fr.lunatech.timekeeper.services.exceptions;

public class CalendarNotFoundException extends BusinessException{

    public CalendarNotFoundException(String message) {
        super(message);
    }
    
    public CalendarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
