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

import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private static Logger logger = LoggerFactory.getLogger(ForbiddenExceptionMapper.class);

    @Override
    public Response toResponse(ForbiddenException e) {
        if (e.getMessage() == null) {
            logger.warn("ForbiddenException", e);
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(
                            Json.createObjectBuilder()
                                    .add("message", "Access to this resource is forbidden for your role.")
                                    .build()
                                    .toString()
                    )
                    .build();
        } else {
            if(logger.isWarnEnabled()) {
                logger.warn(String.format("ForbiddenException %s ", e.getMessage()), e);
            }
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(
                            Json.createObjectBuilder()
                                    .add("message", e.getMessage())
                                    .build().toString()
                    )
                    .build();
        }
    }
}
