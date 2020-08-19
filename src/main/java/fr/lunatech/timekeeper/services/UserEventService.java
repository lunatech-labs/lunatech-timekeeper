package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEventService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    public List<UserEventResponse> getEventsByUserForWeekNumber(Long ownerId, Integer weekNumber, Integer year){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .filter(userEvent -> isUserEventInWeekNumber(userEvent, weekNumber, year))
                .map(UserEventResponse::bind)
                .collect(Collectors.toList());
    }

    public List<UserEventResponse> getEventsByUserForMonthNumber(Long ownerId, Integer monthNumber, Integer year){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .filter(userEvent -> isUserEventInMonthNumber(userEvent, monthNumber, year))
                .map(UserEventResponse::bind)
                .collect(Collectors.toList());
    }

    protected boolean isUserEventInWeekNumber(UserEvent userEvent, Integer weekNumber, Integer year){
        var startDateTimeWeek = TimeKeeperDateUtils.getWeekNumberFromDate(userEvent.startDateTime.toLocalDate());
        var endDateTimeWeek = TimeKeeperDateUtils.getWeekNumberFromDate(userEvent.endDateTime.toLocalDate());
        return (validateYear(userEvent, year))
                && ((startDateTimeWeek <= weekNumber && endDateTimeWeek >= weekNumber)
                || (startDateTimeWeek.equals(weekNumber) || endDateTimeWeek.equals(weekNumber)));
    }

    protected boolean isUserEventInMonthNumber(UserEvent userEvent, Integer monthNumber, Integer year){
        var startDateTimeMonth = userEvent.startDateTime.toLocalDate().getMonthValue();
        var endDateTimeMonth = userEvent.endDateTime.toLocalDate().getMonthValue();
        return (validateYear(userEvent, year))
                && ((startDateTimeMonth <= monthNumber && endDateTimeMonth >= monthNumber)
                || (startDateTimeMonth == monthNumber || endDateTimeMonth == monthNumber));
    }

    protected boolean validateYear(UserEvent userEvent, Integer year){
        return userEvent.startDateTime.getYear() == year || userEvent.endDateTime.getYear() == year;
    }
}
