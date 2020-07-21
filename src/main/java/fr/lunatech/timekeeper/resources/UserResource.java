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

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.UserResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.UserService;
import fr.lunatech.timekeeper.services.responses.UserResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;

public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    public UserResponse me() {
        final var ctx = authentication.context();
        return userService.findResponseById(ctx.getUserId(), ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public List<UserResponse> getAllUsers() {
        final var ctx = authentication.context();
        return userService.listAllResponses(ctx);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public UserResponse getUser(Long id) {
        final var ctx = authentication.context();
        return userService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }
}
