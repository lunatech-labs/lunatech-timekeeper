package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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
    public Long create (EventTemplateRequest request, AuthenticationContext ctx){
        logger.debug("Create a new event template with {}, {}", request, ctx);
        EventTemplate eventTemplate = request.unbind(userService::findById, ctx);
        // by unbinding we also generate userEvent in db for each user
        // user event attributes will be inherited from eventTemplate (startTime, etc.)
        eventTemplate.persistAndFlush();
        return eventTemplate.id;
    }

    @Transactional
    public Optional<Long> update(Long id, EventTemplateRequest request, AuthenticationContext ctx){
        logger.debug("Modify event  template for for id={} with {}, {}", id, request, ctx);
        // early quit: eventTemplate doesn't exist
        final Optional<EventTemplate> maybeEvent = findById(id,ctx);
        if (maybeEvent.isEmpty()){
            return Optional.empty();
        }
        // delete all userEvent associated to the previous state of this event template
        deleteAttendees(maybeEvent.get());

        // actually update the event. by side effect every userEvent associated will be created
        EventTemplate eventTemplateUpdated = request.unbind(maybeEvent.get(),userService::findById, ctx);
        eventTemplateUpdated.persistAndFlush();

        return Optional.of(eventTemplateUpdated.id);
    }

    private Optional<EventTemplate> findById(Long id, AuthenticationContext ctx) {
        return EventTemplate.<EventTemplate>findByIdOptional(id)
                .filter(ctx::canAccess);
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
