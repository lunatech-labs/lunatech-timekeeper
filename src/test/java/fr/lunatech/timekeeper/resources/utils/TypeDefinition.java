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
