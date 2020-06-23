package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class EventTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    @Inject
    UserService userService;

    public List<EventTemplateResponse> listAllResponses(AuthenticationContext ctx){
        return streamAll(ctx,EventTemplateResponse::bind, Collectors.toList());
    }

    @Transactional
    public Long create (EventTemplateRequest request, AuthenticationContext ctx){
        logger.debug("Create a new event template with {}, {}", request, ctx);
        EventTemplate eventTemplate = request.unbind(userService::findById, ctx);
        eventTemplate.persistAndFlush();
        //TODO for each record create a userEvent
        //eventTemplate.associatedUserEvents.forEach(userEvent -> userEventService.create(userEvent));
        eventTemplate.associatedUserEvents.forEach(userEvent -> System.out.println("DIXXX should create user event : " + userEvent));
        return eventTemplate.id;
    }


    <R extends Collection<EventTemplateResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<EventTemplate, EventTemplateResponse> bind,
            Collector<EventTemplateResponse, ?, R> collector
    ) {
        try (final Stream<EventTemplate> eventTemplates = EventTemplate.streamAll()) {
            return eventTemplates
                    //TODO consider filtering like: .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}
