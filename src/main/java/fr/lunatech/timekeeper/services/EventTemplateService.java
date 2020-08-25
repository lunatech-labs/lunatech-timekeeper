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

import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.transaction.Transactional.TxType.MANDATORY;

@ApplicationScoped
public class EventTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    @Inject
    UserService userService;

    public Optional<EventTemplateResponse> getById(Long id, AuthenticationContext context) {
        return EventTemplate.<EventTemplate>findByIdOptional(id)
                .filter(context::canAccess)
                .map(EventTemplateResponse::bind);
    }

    public List<EventTemplateResponse> listAllResponses(AuthenticationContext ctx){
        return streamAll(ctx,EventTemplateResponse::bind, Collectors.toList());
    }

    public List<UserResponse> getAttendees(Long eventId){
        Stream<UserEvent> stream = UserEvent.stream("eventtemplate_id=?1", eventId);
        return stream.map(userEvent -> userEvent.owner)
                .map(UserResponse::bind)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Long> create(EventTemplateRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new event template with {}, {}", request, ctx);
        final EventTemplate eventTemplate = request.unbind(userService::findById, ctx);
        // by unbinding we also generate userEvent in db for each user
        // user event attributes will be inherited from eventTemplate (startTime, etc.)
        boolean checkEvent = validateEvent(
                eventTemplate.name,
                eventTemplate.id,
                eventTemplate.startDateTime,
                eventTemplate.endDateTime, ctx);

        if (checkEvent) {
            return Optional.empty();
        }

        eventTemplate.persistAndFlush();
        return Optional.of(eventTemplate.id);
    }

    @Transactional
    public Optional<Long> update(Long eventId, EventTemplateRequest request, AuthenticationContext ctx) {
        logger.debug("Modify event template for for id={} with {}, {}", eventId, request, ctx);

        // Request coming from frontend
        final var newName = request.getName();
        final var newStartTime = request.getStartDateTime();
        final var newEndTime = request.getEndDateTime();

        // early quit: eventTemplate doesn't exist
        final Optional<EventTemplate> maybeEvent = findById(eventId, ctx);

        return maybeEvent.map(event -> {
            boolean checkEvent = validateEvent(
                    newName,
                    event.id,
                    newStartTime,
                    newEndTime,
                    ctx);
            if (checkEvent) {
                throw new UpdateResourceException(
                        "Event with name: " + request.getName() +
                        ", already exists with same Start: " + request.getStartDateTime().toLocalDate() +
                        "and End: " + request.getEndDateTime().toLocalDate() + " dates."
                );
            }
            // delete all userEvent associated to the previous state of this event template
            deleteAttendees(event);

            // actually update the event. by side effect every userEvent associated will be created
            EventTemplate eventTemplateUpdated = request.unbind(event, userService::findById, ctx);
            eventTemplateUpdated.persistAndFlush();

            return eventTemplateUpdated.id;
        });
    }


    private boolean validateEvent(String name, Long eventId, LocalDateTime startDateTime, LocalDateTime endDateTime, AuthenticationContext ctx) {
        final var startDate = startDateTime.toLocalDate();
        final var endDate = endDateTime.toLocalDate();
        final var foundEvents = findByName(name, ctx)
                .stream()
                .filter(eventTemplate -> !eventTemplate.id.equals(eventId) &&
                        eventTemplate.startDateTime.toLocalDate().isEqual(startDate) &&
                        eventTemplate.endDateTime.toLocalDate().isEqual(endDate))
                .findAny();

        return foundEvents.isPresent();
    }

    private Optional<EventTemplate> findById(Long id, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>findByIdOptional(id)
                .filter(ctx::canAccess);
    }

    private List<EventTemplate> findByName(String name, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>find("name", name)
                .stream()
                .filter(ctx::canAccess)
                .collect(Collectors.toList());
    }

    <R extends Collection<EventTemplateResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<EventTemplate, EventTemplateResponse> bind,
            Collector<EventTemplateResponse, ?, R> collector
    ) {
        try (final Stream<EventTemplate> eventTemplates = EventTemplate.streamAll()) {
            return eventTemplates
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }

    // docs : https://quarkus.io/guides/transaction
    @Transactional(MANDATORY)
    private void deleteAttendees(EventTemplate event) {
        event.attendees
                .forEach(PanacheEntityBase::delete);
    }
}
