package fr.lunatech.timekeeper.resources.utils;

import io.restassured.response.ValidatableResponse;

import static fr.lunatech.timekeeper.resources.utils.VerbDefinition.*;

public class ResourceValidation {

    public static <T> ValidatableResponse putValidation(String uri, String token, T body) {
        return InternalResourceUtils.<T>resourceValidation(PUT, uri, token, body );
    }

    public static <T> ValidatableResponse postValidation(String uri, String token, T body) {
        return InternalResourceUtils.<T>resourceValidation(POST, uri, token, body );
    }

    public static <T> ValidatableResponse deleteValidation(String uri, String token, T body) {
        return InternalResourceUtils.<T>resourceValidation(DELETE, uri, token, body );
    }

    public static <T> ValidatableResponse getValidation(String uri, String token) {
        return InternalResourceUtils.<T>resourceValidation(GET, uri, token);
    }

}
