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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class EventTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    @Inject
    UserEventService userEventService;

    public Optional<EventTemplateResponse> getById(Long id, AuthenticationContext context) {
        return EventTemplate.<EventTemplate>findByIdOptional(id) //NOSONAR
                .filter(context::canAccess)
                .map(eventTemplate -> {
                    var users = userEventService.findAllUsersFromEventTemplate(eventTemplate.id);
                    return EventTemplateResponse.bind(eventTemplate, users);
                });
    }

    public List<EventTemplateResponse> listEvent(Long id, AuthenticationContext ctx) {
        Optional<Long> maybeUserId = Optional.ofNullable(id);
        try (final Stream<EventTemplate> eventTemplates = EventTemplate.streamAll()) { // NOSONAR
            return eventTemplates
                    .filter(ctx::canAccess)
                    .map(t -> {
                        var users = userEventService.findAllUsersFromEventTemplate(t.id);
                        return EventTemplateResponse.bind(t, users);
                    })
                    .filter(element -> maybeUserId.isEmpty() || element.getAttendees().stream().anyMatch(x -> x.getUserId().equals(id)))
                    .collect(Collectors.toList());
        }
    }

    public List<UserResponse> getAttendees(Long eventId) {
        Stream<UserEvent> stream = UserEvent.stream("eventtemplate_id=?1", eventId); //NOSONAR
        return stream.map(userEvent -> userEvent.owner)
                .map(UserResponse::bind)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Long> create(EventTemplateRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new event template with {}, {}", request, ctx);
        final EventTemplate eventTemplate = request.unbind(ctx);

        eventTemplate.isValid();

        // by unbinding we also generate userEvent in db for each user
        // user event attributes will be inherited from eventTemplate (startTime, etc.)
        checkThatNoOtherEventSameNameSameDateAlreadyExists(
                eventTemplate.name,
                eventTemplate.id,
                eventTemplate.startDateTime,
                eventTemplate.endDateTime, ctx);

        eventTemplate.persistAndFlush();

        // Create a userEvent for each attendee using the userEventService
        if (!request.getAttendees().isEmpty()) {
            logger.debug("Create specific userEvents from this eventTemplate {} ", eventTemplate.id);
            findById(eventTemplate.id, ctx).ifPresent(eventTemplate1 -> {
                var created = userEventService.createOrUpdateFromEventTemplate(eventTemplate1, request.getAttendees(), ctx);
                logger.debug("Created {} userEvents from template", created);
            });
        } else {
            logger.debug("No attendees was specified for this eventTemplate");
        }
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
            checkThatNoOtherEventSameNameSameDateAlreadyExists(
                    newName,
                    event.id,
                    newStartTime,
                    newEndTime,
                    ctx);

            // actually update the event
            EventTemplate eventTemplateUpdated = request.unbind(event, ctx);
            eventTemplateUpdated.persistAndFlush();
            logger.debug("Updated eventTemplate id={}", eventTemplateUpdated.id);

            // Create userEvent for each attendee using the userEventService
            // Please note that we will also delete and recreate all userEvents for each attendee.
            findById(eventTemplateUpdated.id, ctx).ifPresent(eventTemplate1 -> {
                var updated = userEventService.createOrUpdateFromEventTemplate(eventTemplate1, request.getAttendees(), ctx);
                logger.debug("Updated {} userEvents from template", updated);
            });

            return eventTemplateUpdated.id;
        });
    }

    protected void checkThatNoOtherEventSameNameSameDateAlreadyExists(String name, Long eventId, LocalDateTime startDateTime, LocalDateTime endDateTime, AuthenticationContext ctx) {
        final var startDate = startDateTime.toLocalDate();
        final var endDate = endDateTime.toLocalDate();
        final var foundEvents = findByName(name, ctx)
                .stream()
                .filter(eventTemplate -> !eventTemplate.id.equals(eventId) &&
                        eventTemplate.startDateTime.toLocalDate().isEqual(startDate) &&
                        eventTemplate.endDateTime.toLocalDate().isEqual(endDate))
                .findAny();

        if (foundEvents.isPresent()) {
            throw new UpdateResourceException(
                    "Event with name: " + name +
                            ", already exists with same start " + startDateTime.toLocalDate() +
                            " and end " + endDateTime.toLocalDate() + " dates."
            );
        }
    }

    private Optional<EventTemplate> findById(Long id, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>findByIdOptional(id) //NOSONAR
                .filter(ctx::canAccess);
    }

    private List<EventTemplate> findByName(String name, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>find("name", name) //NOSONAR
                .stream()
                .filter(ctx::canAccess)
                .collect(Collectors.toList());
    }

}
