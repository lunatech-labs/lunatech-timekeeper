/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.resources.utils;

import io.restassured.response.ValidatableResponse;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;
import static java.lang.String.format;

public class ResourceReader {

    /**
     * Reads one resource from API and return the result with type defined by responseType parameter
     * @param id - Resource id
     * @param responseType type of instance returned by read method
     * @param token API token
     * @param <R> Concret type returned by read method
     * @return Resource
     */
    public static <R> R read(Long id, Class<R> responseType, String token) {
        final String uriResource;
        if (responseType == ProjectDef.typeDef.ResponseType) {
            uriResource = ProjectDef.uri;
        } else if (responseType == ClientDef.typeDef.ResponseType) {
            uriResource = ClientDef.uri;
        } else if (responseType == UserDef.typeDef.ResponseType) {
            uriResource = UserDef.uri;
        } else if (responseType == OrganizationDef.typeDef.ResponseType) {
            uriResource = OrganizationDef.uri;
        } else {
            throw new IllegalStateException(format("ResourceType provided is unknown  %s", responseType));
        }

        return InternalResourceUtils.<R>readResource(id, uriResource, responseType, token);
    }

    /**
     * Reads Resource from API and returns the corresponding validatable object to apply assertions
     * @param uriResource
     * @param token
     * @return
     */
    @Deprecated
    public static ValidatableResponse readValidation(Long id, String uriResource, String token) {
        return InternalResourceUtils.readResourceValidation(id, uriResource, token);
    }

    /**
     * Reads Resource from API and returns the corresponding validatable object to apply assertions
     * @param uriResource
     * @param token
     * @return
     */
    @Deprecated
    public static ValidatableResponse readValidation(String uriResource, String token) {
        return InternalResourceUtils.readResourceValidation(uriResource, token);
    }


}
