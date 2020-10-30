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

import fr.lunatech.timekeeper.resources.openapi.UserAvailabilityResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.UserEventService;
import fr.lunatech.timekeeper.services.UserService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class UserAvailabilityResource implements UserAvailabilityResourceApi {
    @Inject
    UserService userService;

    @Inject
    UserEventService userEventService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetAvailabilities", description = "Counts how many times the user to get the organization on method 'getAvailabilities'")
    @Timed(name = "timeGetAvailabilities", description = "Times how long it takes the user to get the organization on method 'getAvailabilities'", unit = MetricUnits.MILLISECONDS)
    public List<Object> getAvailabilities(LocalDate start, LocalDate end) {
        return Collections.emptyList();
    }
}
