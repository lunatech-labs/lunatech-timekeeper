/*
 * Copyright 2020 Lunatech S.A.S
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
        return InternalResourceUtils.<T>resourceValidation(uri, token);
    }

}
