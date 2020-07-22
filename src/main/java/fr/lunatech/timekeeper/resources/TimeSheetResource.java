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

import fr.lunatech.timekeeper.resources.openapi.TimeSheetResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

public class TimeSheetResource implements TimeSheetResourceApi {

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;


    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetTimeSheet", description = "Counts how many times the user to get the time sheet on method 'getTimeSheet'")
    @Timed(name = "timeGetTimeSheet", description = "Times how long it takes the user to get the time sheet on method 'getTimeSheet'", unit = MetricUnits.MILLISECONDS)
    public TimeSheetResponse getTimeSheet(Long id) {
        final var ctx = authentication.context();
        return timeSheetService.findTimeSheetById(id,ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countUpdateTimeSheet", description = "Counts how many times the user to update the time sheet on method 'updateTimeSheet'")
    @Timed(name = "timeUpdateTimeSheet", description = "Times how long it takes the user to update the time sheet on method 'updateTimeSheet'", unit = MetricUnits.MILLISECONDS)
    public Response updateTimeSheet(Long id, TimeSheetRequest request) {
        final var ctx = authentication.context();
        return timeSheetService.update(id, request, ctx)
                .map(it ->  Response.noContent().build())
                .orElseThrow(NotFoundException::new);
    }
}
