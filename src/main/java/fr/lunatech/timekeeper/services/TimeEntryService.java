package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class TimeEntryService {
    private static Logger logger = LoggerFactory.getLogger(TimeEntryService.class);

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    @Transactional
    public Long createTimeEntry(Long timeSheetId, TimeEntryRequest request, AuthenticationContext ctx, Enum TimeUnit) {
        logger.debug("Create a new TimeEntry with {}, {}", request, ctx);
        final TimeEntry timeEntry = request.unbind( timeSheetId, timeSheetService::findById, ctx);
        try {
            timeEntry.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException(String.format("TimeEntry was not created due to constraint violation"));
        }
        return timeEntry.id;
    }

}
