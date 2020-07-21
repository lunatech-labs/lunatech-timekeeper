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

package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.resources.exceptions.ConflictOnVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler that should trigger a 409 Conflict if there is an issue on the Version number
 */
@Provider
public class ConflictOnVersionExceptionMapper implements ExceptionMapper<ConflictOnVersionException> {
    private static Logger logger = LoggerFactory.getLogger(ConflictOnVersionExceptionMapper.class);

    @Override
    public Response toResponse(ConflictOnVersionException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("message", String.format("%s", e.getMessage())) // e.getMessage can be null, but JSON format requires a value.
                        .build()
                        .toString()
                )
                .build();
    }
}
