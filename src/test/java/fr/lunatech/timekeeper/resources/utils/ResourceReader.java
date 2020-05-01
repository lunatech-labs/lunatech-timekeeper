package fr.lunatech.timekeeper.resources.utils;

import io.restassured.response.ValidatableResponse;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;
import static java.lang.String.format;

public class ResourceReader {

    public static <R> R read(Long id, Class<R> responseType, String token) {
        final String uriResource;
        if (responseType == ProjectDef.typeDef.ResponseType) {
            uriResource = "/api/projects";
        } else if (responseType == ClientDef.typeDef.ResponseType) {
            uriResource = "/api/clients";
        } else if (responseType == UserDef.typeDef.ResponseType) {
            uriResource = "/api/users";
        } else if (responseType == OrganizationDef.typeDef.ResponseType) {
            uriResource = "/api/organizations";
        } else {
            throw new IllegalStateException(format("ResourceType provided is unknown  %s", responseType));
        }

        return AbstractResourceFactory.<R>readResource(id, uriResource, responseType, token);
    }

    public static ValidatableResponse readValidation(String uriResource, String token) {
        return AbstractResourceFactory.readResourceValidation(uriResource, token);
    }


}
