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
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

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
    @Counted(name = "countMe", description = "Counts how many times the user load his information on method 'me'")
    @Timed(name = "timeMe", description = "Times how long it takes the user load his information on method 'me'", unit = MetricUnits.MILLISECONDS)
    public UserResponse me() {
        final var ctx = authentication.context();
        return userService.findResponseById(ctx.getUserId(), ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetAllUsers", description = "Counts how many times the user load the organization list on method 'getAllUsers'")
    @Timed(name = "timeGetAllUsers", description = "Times how long it takes the user load the organization list on method 'getAllUsers'", unit = MetricUnits.MILLISECONDS)
    public List<UserResponse> getAllUsers() {
        final var ctx = authentication.context();
        return userService.listAllResponses(ctx);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetUser", description = "Counts how many times the user to get the organization on method 'getUser'")
    @Timed(name = "timeGetUser", description = "Times how long it takes the user to get the organization on method 'getUser'", unit = MetricUnits.MILLISECONDS)
    public UserResponse getUser(Long id) {
        final var ctx = authentication.context();
        return userService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }
}
