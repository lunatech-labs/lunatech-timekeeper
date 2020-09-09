package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEventService {

    @Inject
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserEventService.class);

    public List<UserEventResponse> getEventsByUserForWeekNumber(Long ownerId, Integer weekNumber, Integer year) {
        return getEventsByUser(ownerId, weekNumber, year, TimeKeeperDateUtils::getWeekNumberFromDate);
    }

    public List<UserEventResponse> getEventsByUserForMonthNumber(Long ownerId, Integer monthNumber, Integer year) {
        return getEventsByUser(ownerId, monthNumber, year, TimeKeeperDateUtils::getMonthNumberFromDate);
    }

    protected List<UserEventResponse> getEventsByUser(Long ownerId, Integer monthNumber, Integer year, ToIntFunction<LocalDate> getWeekOfMonthNumberFromDate) {
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId) //NOSONAR
                .filter(userEvent -> isUserEventInWeekOrMonthNumber(userEvent, monthNumber, year, getWeekOfMonthNumberFromDate))
                .map(UserEventResponse::bind)
                .collect(Collectors.toList());
    }

    protected boolean isUserEventInWeekOrMonthNumber(UserEvent userEvent, Integer weekOrMonthNumber, Integer year, ToIntFunction<LocalDate> getWeekOfMonthNumberFromDate) {
        var startDateTimeNumber = getWeekOfMonthNumberFromDate.applyAsInt(userEvent.startDateTime.toLocalDate());
        var endDateTimeNumber = getWeekOfMonthNumberFromDate.applyAsInt(userEvent.endDateTime.toLocalDate());
        return (validateYear(userEvent, year))
                && ((startDateTimeNumber <= weekOrMonthNumber && endDateTimeNumber >= weekOrMonthNumber)
                || (startDateTimeNumber == weekOrMonthNumber || endDateTimeNumber == weekOrMonthNumber));
    }

    protected boolean validateYear(UserEvent userEvent, Integer year) {
        return userEvent.startDateTime.getYear() == year || userEvent.endDateTime.getYear() == year;
    }

    protected List<User> findAllUsersFromEventTemplate(Long templateId){
        return UserEvent.<UserEvent>stream("eventtemplate_id=?1",templateId) // NOSONAR
                .map( userEvent -> userEvent.owner)
                .collect(Collectors.toList());
    }

    public Long createOrUpdateFromEventTemplate(EventTemplate eventTemplate,
                                                List<EventTemplateRequest.UserEventRequest> userEventRequests,
                                                AuthenticationContext ctx) {
        if (userEventRequests.isEmpty()) {
            return 0L;
        }

        // Here we delete any userEvent that was previously created with this template.
        // If you remove a user from a template, it will delete the associated userEvent
        UserEvent.<UserEvent>stream("eventtemplate_id=?1",eventTemplate.id) // NOSONAR
                .forEach(PanacheEntityBase::delete);

        long updated = 0;
        for (var userEventRequest : userEventRequests) {
            var userEvent = userEventRequest.unbind(eventTemplate, userService::findById, ctx);
            userEvent.persistAndFlush();
            updated++;
        }
        return updated;
    }
}
