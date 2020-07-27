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

import fr.lunatech.timekeeper.resources.openapi.TimeEntryResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.TimeEntryService;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TimeEntryResource implements TimeEntryResourceApi {

    @Inject
    TimeEntryService timeEntryService;

    @Inject
    AuthenticationContextProvider authentication;


    static class TimeEntriesNumberOfHoursGauge {
        int fourHours;
        int eightHours;
        int otherHours;
    }
    private TimeEntriesNumberOfHoursGauge timeEntriesNumberOfHoursGauge = new TimeEntriesNumberOfHoursGauge();

    @RolesAllowed({"user"})
    @Override
    @Counted(name = "countCreateTimeEntry", description = "Counts how many times the user create an time entry on method 'createTimeEntry'")
    @Timed(name = "timeCreateTimeEntry", description = "Times how long it takes the user create an time entry on method 'createTimeEntry'", unit = MetricUnits.MILLISECONDS)
    public Response createTimeEntry(@NotNull Long timeSheetId, @Valid TimeEntryRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        switch (request.getNumberHours()) {
            case 4:
                timeEntriesNumberOfHoursGauge.fourHours++;
                break;
            case 8:
                timeEntriesNumberOfHoursGauge.eightHours++;
                break;
            default:
                timeEntriesNumberOfHoursGauge.otherHours++;
                break;
        }
        final long timeId = timeEntryService.createTimeEntry(timeSheetId, request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(timeId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user"})
    @Override
    @Counted(name = "countUpdateTimeEntry", description = "Counts how many times the user to update the time entry on method 'updateTimeEntry'")
    @Timed(name = "timeUpdateTimeEntry", description = "Times how long it takes the user to update the time entry on method 'updateTimeEntry'", unit = MetricUnits.MILLISECONDS)
    public Response updateTimeEntry(Long timeSheetId, Long timeEntryId, @Valid TimeEntryRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        return timeEntryService.updateTimeEntry(timeSheetId, timeEntryId, request, ctx)
                .map(it -> Response.noContent().build())
                .orElseThrow(NotFoundException::new);
    }

    @Gauge(name = "numberOfHalfDayEntries", unit = MetricUnits.NONE, description = "Number of entries created for a half day")
    public Integer numberOfHalfDayEntries() {
//        return timeEntriesNumberOfHoursGauge.fourHours;
        return 4;
    }


    @Gauge(name = "numberOfFullDayEntries", unit = MetricUnits.NONE, description = "Number of entries created for a full day")
    public Integer numberOfDayEntries() {
//        return timeEntriesNumberOfHoursGauge.eightHours;
        return 8;
    }

    @Gauge(name = "numberOfOtherHoursEntries", unit = MetricUnits.NONE, description = "Number of entries created with an amount of hours different than 4 and 8 hours")
    public Integer numberOfOtherHoursEntries() {
//        return timeEntriesNumberOfHoursGauge.otherHours;
        return 3;
    }

//    @Gauge(name = "numberOfOtherHoursEntries", unit = MetricUnits.NONE, description = "Number of entries created with an amount of hours different than 1, 4 and 8 hours")
    public Response yolo() {
        return Response.ok(timeEntriesNumberOfHoursGauge.otherHours).build();
    }
}
