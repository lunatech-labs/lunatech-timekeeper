package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEventService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    public List<UserEvent> getEventsByUserForWeekNumber(Long ownerId, Integer weekNumber){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .filter(userEvent -> getUserEventForWeekNumber(userEvent, weekNumber))
                .collect(Collectors.toList());
    }

    public List<UserEvent> getEventsByUserForMonthNumber(Long ownerId, Integer monthNumber){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .filter(userEvent -> getUserEventForMonthNumber(userEvent, monthNumber))
                .collect(Collectors.toList());
    }

    protected boolean getUserEventForWeekNumber(UserEvent userEvent, Integer weekNumber){
        var startDateTimeWeek = TimeKeeperDateUtils.getWeekNumberFromDate(userEvent.startDateTime.toLocalDate());
        var endDateTimeWeek = TimeKeeperDateUtils.getWeekNumberFromDate(userEvent.endDateTime.toLocalDate());
        return (startDateTimeWeek <= weekNumber && endDateTimeWeek >= weekNumber) || (startDateTimeWeek.equals(weekNumber) || endDateTimeWeek.equals(weekNumber));
    }

    protected boolean getUserEventForMonthNumber(UserEvent userEvent, Integer monthNumber){
        var startDateTimeMonth = userEvent.startDateTime.toLocalDate().getMonthValue();
        var endDateTimeMonth = userEvent.endDateTime.toLocalDate().getMonthValue();
        return (startDateTimeMonth <= monthNumber && endDateTimeMonth >= monthNumber) || (startDateTimeMonth == monthNumber || endDateTimeMonth == monthNumber);
    }
}
