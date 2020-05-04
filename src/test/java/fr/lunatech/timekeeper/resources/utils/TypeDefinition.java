package fr.lunatech.timekeeper.resources.utils;

public class TypeDefinition<R, T> {

    final public Class<R> requestType;
    final public Class<T> ResponseType;

    private TypeDefinition(Class<R> requestType, Class<T> responseType) {
        this.requestType = requestType;
        this.ResponseType = responseType;
    }

    static <R, T> TypeDefinition apply(Class<R> requestType, Class<T> responseType) {
        return new TypeDefinition(requestType, responseType);
    }
}
