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

package fr.lunatech.timekeeper.resources.handlers;

import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalEntityStateExceptionMapper implements ExceptionMapper<IllegalEntityStateException> {

    private static Logger logger = LoggerFactory.getLogger(IllegalEntityStateExceptionMapper.class);

    @Override
    public Response toResponse(IllegalEntityStateException e) {
        logger.warn(String.format("IllegalEntityStateException %s ", e.getMessage()));
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("message", e.getMessage()).build()
                )
                .build();
    }
}
