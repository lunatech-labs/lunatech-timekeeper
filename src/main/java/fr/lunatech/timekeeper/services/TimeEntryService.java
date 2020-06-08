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

@ApplicationScoped
public class TimeEntryService {
    private static Logger logger = LoggerFactory.getLogger(TimeEntryService.class);

    @Inject
    protected TimeSheetService timeSheetService;

    @Transactional
    public Long createTimeEntry(Long timeSheetId, TimeEntryRequest request, AuthenticationContext ctx, Enum TimeUnit) {
        logger.debug("Create a new TimeEntry with {}, {}", request, ctx);
        // TODO check that the user can create the timeEntry for this time entry
        final TimeEntry timeEntry = request.unbind( timeSheetId, timeSheetService::findById, ctx);
        try {
            timeEntry.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException(String.format("TimeEntry was not created due to constraint violation"));
        }
        return timeEntry.id;
    }

}
