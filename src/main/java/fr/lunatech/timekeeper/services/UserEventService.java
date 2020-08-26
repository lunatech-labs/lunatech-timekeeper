package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEventService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    public List<UserEventResponse> getEventsByUserForWeekNumber(Long ownerId, Integer weekNumber, Integer year){
        return getEventsByUser(ownerId,weekNumber,year,TimeKeeperDateUtils::getWeekNumberFromDate);
    }

    public List<UserEventResponse> getEventsByUserForMonthNumber(Long ownerId, Integer monthNumber, Integer year){
        return getEventsByUser(ownerId,monthNumber,year,TimeKeeperDateUtils::getMonthNumberFromDate);
    }

    protected List<UserEventResponse> getEventsByUser(Long ownerId, Integer monthNumber, Integer year, ToIntFunction<LocalDate> getWeekOfMonthNumberFromDate){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .filter(userEvent -> isUserEventInWeekOrMonthNumber(userEvent, monthNumber, year, getWeekOfMonthNumberFromDate))
                .map(UserEventResponse::bind)
                .collect(Collectors.toList());
    }
    protected boolean isUserEventInWeekOrMonthNumber(UserEvent userEvent, Integer weekOrMonthNumber, Integer year, ToIntFunction<LocalDate> getWeekOfMonthNumberFromDate){
        var startDateTimeNumber = getWeekOfMonthNumberFromDate.applyAsInt(userEvent.startDateTime.toLocalDate());
        var endDateTimeNumber = getWeekOfMonthNumberFromDate.applyAsInt(userEvent.endDateTime.toLocalDate());
        return (validateYear(userEvent, year))
                && ((startDateTimeNumber <= weekOrMonthNumber && endDateTimeNumber >= weekOrMonthNumber)
                || (startDateTimeNumber == weekOrMonthNumber || endDateTimeNumber == weekOrMonthNumber));
    }

    protected boolean validateYear(UserEvent userEvent, Integer year){
        return userEvent.startDateTime.getYear() == year || userEvent.endDateTime.getYear() == year;
    }
}
