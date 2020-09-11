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
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
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

    public List<EventTemplateResponse> listAll(AuthenticationContext ctx) {
        try (final Stream<EventTemplate> eventTemplates = EventTemplate.streamAll()) { // NOSONAR
            return eventTemplates
                    .filter(ctx::canAccess)
                    .map(t -> {
                        var users = userEventService.findAllUsersFromEventTemplate(t.id);
                        return EventTemplateResponse.bind(t, users);
                    })
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

        if (request.getStartDateTime() == null) {
            throw new CreateResourceException("Cannot create an EventTemplate if startDateTime is null");
        }
        if (request.getEndDateTime() == null) {
            throw new CreateResourceException("Cannot create an EventTemplate if endDateTime is null");
        }

        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new CreateResourceException("Cannot create an EventTemplate if startDateTime is after endDateTime");
        }

        try {
            eventTemplate.persistAndFlush();
        }catch (PersistenceException pe){
            if(pe.getCause() instanceof org.hibernate.exception.ConstraintViolationException && pe.getCause().getCause() instanceof  org.postgresql.util.PSQLException){
                    logger.warn(String.format("SQL Exception : unable to persist this EventTemplate due to [%s]",pe.getCause().getCause().getMessage()) );
                    throw new CreateResourceException("Cannot create EventTemplate due to database constraints. Check that no other eventTemplate with sameDate, same name already exists");
            }
            throw new CreateResourceException(String.format("Unable to persist this EventTemplate due to [%s]",pe.getMessage()));
        }

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

        if (request.getStartDateTime() == null) {
            throw new UpdateResourceException("Cannot update an EventTemplate if startDateTime is null");
        }
        if (request.getEndDateTime() == null) {
            throw new UpdateResourceException("Cannot update an EventTemplate if endDateTime is null");
        }

        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new UpdateResourceException("Cannot update an EventTemplate if startDateTime is after endDateTime");
        }

        final Optional<EventTemplate> maybeEvent = findById(eventId, ctx);

        return maybeEvent.map(event -> {
            // actually update the event
            EventTemplate eventTemplateUpdated = request.unbind(event, ctx);

            try {
                eventTemplateUpdated.persistAndFlush();
            }catch (PersistenceException pe){
                if(pe.getCause() instanceof org.hibernate.exception.ConstraintViolationException && pe.getCause().getCause() instanceof  org.postgresql.util.PSQLException){
                    logger.warn(String.format("SQL Exception : unable to persist this EventTemplate due to [%s]",pe.getCause().getCause().getMessage()) );
                    throw new UpdateResourceException("Cannot create EventTemplate due to database constraints. Check that no other eventTemplate with sameDate, same name already exists");
                }
                throw new CreateResourceException(String.format("Unable to persist this EventTemplate due to [%s]",pe.getMessage()));
            }

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

    private Optional<EventTemplate> findById(Long id, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>findByIdOptional(id) //NOSONAR
                .filter(ctx::canAccess);
    }

}