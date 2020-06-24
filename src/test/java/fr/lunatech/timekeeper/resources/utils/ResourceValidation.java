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

import java.util.Map;

import static fr.lunatech.timekeeper.resources.utils.VerbDefinition.*;

public class ResourceValidation {

    public static <T> ValidatableResponse putValidation(String uri, String token, T body, javax.ws.rs.core.Response.Status status) {
        return InternalResourceUtils.<T>resourceValidation(PUT, uri, token, body, status);
    }

    public static <T> ValidatableResponse postValidation(String uri, String token, T body, javax.ws.rs.core.Response.Status status) {
        return InternalResourceUtils.<T>resourceValidation(POST, uri, token, body, status);
    }

    public static <T> ValidatableResponse deleteValidation(String uri, String token, T body, javax.ws.rs.core.Response.Status status) {
        return InternalResourceUtils.<T>resourceValidation(DELETE, uri, token, body, status);
    }

    public static <T> ValidatableResponse getValidation(String uri, String token, javax.ws.rs.core.Response.Status status) {
        return InternalResourceUtils.<T>resourceValidation(GET, uri, token, status);
    }

    public static <T> ValidatableResponse getValidation(String uri, Map<String, String> params, String token, javax.ws.rs.core.Response.Status status) {
        return InternalResourceUtils.<T>resourceValidation(GET, uri, token, status);
    }
}
