package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventTemplate;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Optional<UserEventResponse> getUserEventById(Long id, AuthenticationContext context) { //NOSONAR
        return UserEvent.<UserEvent>findByIdOptional(id) //NOSONAR
                .map(UserEventResponse::bind);
    }

    public List<UserEventResponse> getAllPersonalEventsForAnUser(Long ownerId, AuthenticationContext context) { //NOSONAR
        final var maybeParam = Optional.ofNullable(ownerId);
        Stream<UserEvent> stream = maybeParam.isPresent() ? UserEvent.stream("owner_id=?1", ownerId) : UserEvent.streamAll(); //NOSONAR
        return stream.map(UserEventResponse::bind)
                .collect(Collectors.toList());
    }

    protected List<UserEventResponse> getEventsByUser(Long ownerId, Integer monthNumber, Integer year, ToIntFunction<LocalDate> getWeekOfMonthNumberFromDate) {
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId) //NOSONAR
                .filter(userEvent -> isUserEventInWeekOrMonthNumber(userEvent, monthNumber, year, getWeekOfMonthNumberFromDate))
                .map(UserEventResponse::bind) //NOSONAR
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

    protected List<User> findAllUsersFromEventTemplate(Long templateId) {
        return UserEvent.<UserEvent>stream("eventtemplate_id=?1", templateId) // NOSONAR
                .map(userEvent -> userEvent.owner)
                .collect(Collectors.toList());
    }

    public Long createOrUpdateFromEventTemplate(EventTemplate eventTemplate,
                                                List<UserEventRequest> userEventRequests,
                                                AuthenticationContext ctx) {
        // Here we delete any userEvent that was previously created with this template.
        // If you remove a user from a template, it will delete the associated userEvent
        UserEvent.<UserEvent>stream("eventtemplate_id=?1", eventTemplate.id) // NOSONAR
                .forEach(PanacheEntityBase::delete);

        if (userEventRequests.isEmpty()) {
            return 0L;
        }

        long updated = 0;
        for (var userEventRequest : userEventRequests) {
            var userEvent = userEventRequest.unbind(eventTemplate, userService::findById, ctx);

            if(isUserAvailableForDates(userEvent.owner.id, userEvent.startDateTime, userEvent.endDateTime)){
                userEvent.persistAndFlush();
                updated++;
            }else{
                if(logger.isWarnEnabled()) {
                    logger.warn(String.format("Cannot persist a userEvent for user %s as the user is already booked for those dates.", userEvent.owner.getFullName()));
                }
            }

        }
        return updated;
    }

    /**
     * add uservent rules
     *
     * @param request
     * @param ctx
     * @return
     */
    @Transactional
    public Optional<Long> create(UserEventRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new user event with {}, {}", request, ctx);
        final UserEvent userEvent = request.unbind(userService::findById, ctx);
        userEvent.persistAndFlush();
        return Optional.of(userEvent.id);
    }

    /**
     * Returns true if the specified user is available for the specified date range
     *
     * @param userId        cannot be null
     * @param startDateTime is a valid startDateTime and cannot be null
     * @param endDateTime   if specified, must be after startDateTime
     * @return true if the user is available, otherwise returns false
     */
    protected boolean isUserAvailableForDates(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("startDateTime cannot be null");
        }

        LocalDateTime localEndDateTime = endDateTime == null ? startDateTime : endDateTime;
        Optional<UserEvent> maybeOneEvent;
        // Please not that it is on purpose that we check that the localEndDateTime is strictly lower than thestartdatetime
        maybeOneEvent = UserEvent.<UserEvent>stream("owner_id=?1 and startdatetime<?2 and enddatetime>=?3", userId, localEndDateTime, startDateTime).findFirst(); //NOSONAR
        return maybeOneEvent.isEmpty();
    }

    /**
     * Returns a list of all userEvents
     * 
     * @param context use to filter events by organization
     * @return the list of userEvents
     */
    public List<UserEventResponse> listAllEventUser(AuthenticationContext context){ //NOSONAR
        try (final Stream<UserEvent> userEvent = UserEvent.streamAll()) { //NOSONAR
            return userEvent.map(UserEventResponse::bind).collect(Collectors.toList());//NOSONAR
        }
    }
}
