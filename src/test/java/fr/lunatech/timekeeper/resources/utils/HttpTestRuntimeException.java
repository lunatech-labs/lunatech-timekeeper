package fr.lunatech.timekeeper.resources.utils;

/**
 * A Simple utility class that is used to catch RESTEasy HTTP error and convert the result into
 * a simple RuntimeException
 */
public class HttpTestRuntimeException extends RuntimeException{
    private Integer httpStatus;
    private String httpMessage;
    private String mimeType;

    public HttpTestRuntimeException(Integer httpStatus, String httpMessage, String mimeType) {
        this.httpStatus = httpStatus;
        this.httpMessage = httpMessage;
        this.mimeType = mimeType;
    }

    public HttpTestRuntimeException(String message, Integer httpStatus, String httpMessage, String mimeType) {
        super(message);
        this.httpStatus = httpStatus;
        this.httpMessage = httpMessage;
        this.mimeType = mimeType;
    }

    public HttpTestRuntimeException(String message, Throwable cause, Integer httpStatus, String httpMessage, String mimeType) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.httpMessage = httpMessage;
        this.mimeType = mimeType;
    }

    public HttpTestRuntimeException(Throwable cause, Integer httpStatus, String httpMessage, String mimeType) {
        super(cause);
        this.httpStatus = httpStatus;
        this.httpMessage = httpMessage;
        this.mimeType = mimeType;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public String getMimeType() {
        return mimeType;
    }
}
