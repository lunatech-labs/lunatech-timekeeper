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

package fr.lunatech.timekeeper.resources.exceptions;

/**
 * This is a marker Exception to report that the Resource could not be updated.
 */
public class UpdateResourceException extends RuntimeException {
    public UpdateResourceException() {
        super();
    }

    public UpdateResourceException(String message) {
        super(message);
    }

    public UpdateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateResourceException(Throwable cause) {
        super(cause);
    }
}
