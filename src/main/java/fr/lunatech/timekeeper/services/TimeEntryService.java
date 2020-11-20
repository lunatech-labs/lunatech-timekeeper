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

package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.gauges.TimeEntriesNumberPerHoursGauge;
import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.security.auth.DestroyFailedException;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@ApplicationScoped
public class TimeEntryService {
    private static Logger logger = LoggerFactory.getLogger(TimeEntryService.class);

    @Inject
    protected TimeSheetService timeSheetService;

    @Inject
    protected TimeEntriesNumberPerHoursGauge timeEntriesNumberPerHoursGauge;

    @Transactional
    public Long createTimeEntry(Long timeSheetId, TimeEntryRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new TimeEntry with {}, {}", request, ctx);
        final TimeEntry timeEntry = request.unbind(timeSheetId, timeSheetService::findById, ctx);
        if (!ctx.canCreate(timeEntry)) {
            throw new ForbiddenException("The user can't add an entry to the time sheet with id : " + timeSheetId);
        }
        try {
            timeEntry.persistAndFlush();
            timeEntriesNumberPerHoursGauge.incrementGauges(request.getNumberOfHours());
        } catch (PersistenceException pe) {
            throw new CreateResourceException("TimeEntry was not created due to constraint violation");
        }
        return timeEntry.id;
    }

    @Transactional
    public Optional<Long> updateTimeEntry(Long timeSheetId, Long timeEntryId, TimeEntryRequest request, AuthenticationContext ctx) {
        logger.debug("Modify timeEntry for id={} with {}, {}", timeSheetId, request, ctx);
        Optional<TimeEntry> timeEntryOptional = findById(timeEntryId, ctx);
        return timeEntryOptional
                .map(timeEntry -> request.unbind(timeEntry, timeSheetId, timeSheetService::findById, ctx))
                .map(timeEntry -> timeEntry.id);
    }

    Optional<TimeEntry> findById(Long id, AuthenticationContext ctx) {
        return TimeEntry.<TimeEntry>findByIdOptional(id)
                .filter(ctx::canAccess);
    }


    @Transactional
    public Optional<Long> deleteTimeEntry(Long timeSheetId, Long timeEntryId, AuthenticationContext ctx) {
        logger.debug("Delete timeEntry for timeSheetId={} with timeEntryId={}, request={}, ctx={}", timeSheetId, timeEntryId, ctx);
        Optional<TimeEntry> timeEntryOptional = findById(timeEntryId, ctx);

        if (!timeEntryOptional.isPresent()) {
            throw new NotFoundException("The user can't delete time entry with Id [ " + timeEntryId + " ], from the time sheet with Id [ " + timeSheetId + " ]");
        }

        try {
            var timeEntry = timeEntryOptional.get();

            if (!ctx.canAccess(timeEntry)) {
                throw new ForbiddenException("The user can't delete time entry with Id [ " + timeEntryId + " ], from the time sheet with Id [ " + timeSheetId + " ]");
            }

            timeEntry.delete();

        } catch (Exception e) {
            throw new RuntimeException("The time entry deletion failed for time entry with Id [ " + timeEntryId + " ], from the time sheet with Id [ " + timeSheetId + " ]");
        }

        return Optional.of(timeEntryId);
    }
}