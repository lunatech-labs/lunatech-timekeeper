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

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TimeSheetService {

    private static Logger logger = LoggerFactory.getLogger(TimeSheetService.class);

    Boolean userHasNoTimeSheet(Long projectId, Long userId) {
        return TimeSheet.stream("user_id = ?1 and project_id = ?2", userId, projectId).count() == 0;
    }

    public Optional<TimeSheetResponse> findTimeSheetById(Long id, AuthenticationContext ctx) {
        return findById(id, ctx).map(TimeSheetResponse::bind);
    }

    @Transactional
    Long createDefaultTimeSheet(Project project, User owner, AuthenticationContext ctx) {
        // By default, the start date must be the date of creation
        final LocalDate startDate = LocalDate.now();
        TimeSheet timeSheet = new TimeSheet(project, owner, TimeUnit.HOURLY, project.getBillable(), null, null, TimeUnit.DAY, Collections.emptyList(), startDate);
        logger.debug("Create a default timesheet with {}, {}", timeSheet, ctx);
        try {
            timeSheet.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException("Timesheet was not created due to constraint violation");
        }
        return timeSheet.id;
    }

    @Transactional
    public Optional<Long> update(Long id, TimeSheetRequest request, AuthenticationContext ctx) {
        logger.info("Modify timesheet for id={} with {}, {}", id, request, ctx);
        return findById(id, ctx)
                .map(request::unbind)
                .map(timeSheet -> timeSheet.id);
    }

    public List<TimeSheetResponse> findAllActivesForUser(AuthenticationContext ctx) {
        return streamAllActive(ctx, TimeSheetResponse::bind, Collectors.toList());
    }

    public Optional<TimeSheetResponse> findFirstForProjectForUser(long idProject, long idUser) {
        final Stream<TimeSheet> timeSheetStream = TimeSheet.stream("project_id= ?1 AND user_id= ?2", idProject, idUser);
        return timeSheetStream.map(TimeSheetResponse::bind)
                .findFirst();
    }

    // for the MVP we don't filter, some kind of "active" flag will be added once business rules are defined (see TK-389)
    <R extends Collection<TimeSheetResponse>> R streamAllActive(
            AuthenticationContext ctx,
            Function<TimeSheet, TimeSheetResponse> bind,
            Collector<TimeSheetResponse, ?, R> collector
    ) {
        try (final Stream<TimeSheet> ts = TimeSheet.stream("user_id = ?1", ctx.getUserId())) {
            return ts
                    .map(bind)
                    .collect(collector);
        }
    }

    Optional<TimeSheet> findById(Long id, AuthenticationContext ctx) {
        return TimeSheet.<TimeSheet>findByIdOptional(id)
                .filter(ctx::canAccess);
    }
}
