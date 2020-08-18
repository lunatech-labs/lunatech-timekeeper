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

import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CreateResourceExceptionMapper implements ExceptionMapper<CreateResourceException> {
    private static Logger logger = LoggerFactory.getLogger(CreateResourceExceptionMapper.class);

    @Override
    public Response toResponse(CreateResourceException e) {
        if(logger.isWarnEnabled()) {
            logger.warn(String.format("CreateResourceException: %s", e.getMessage()), e);
        }
        // We could use also a CONFLICT but since we do not check the exact constraint name, we prefer to use a
        // more generic "Bad request" http response here.
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(
                        Json.createObjectBuilder()
                        // e.getMessage can be null, but JSON format requires a value. This is why there is a String.format here
                        .add("message", String.format("%s", e.getMessage()))
                        .build()
                        .toString()
                )
                .build();
    }
}
