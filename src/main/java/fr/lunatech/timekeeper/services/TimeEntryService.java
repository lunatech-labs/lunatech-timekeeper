package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.TimeEntry;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeEntryRequest;
import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class TimeEntryService {
    private static Logger logger = LoggerFactory.getLogger(TimeEntryService.class);

    @Inject
    protected TimeSheetService timeSheetService;

    @Transactional
    public Long createTimeEntry(Long timeSheetId, TimeEntryRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new TimeEntry with {}, {}", request, ctx);
        final TimeEntry timeEntry = request.unbind(timeSheetId, timeSheetService::findById, ctx);
        if (!ctx.canCreate(timeEntry)) {
            throw new ForbiddenException("The user can't add an entry to the time sheet with id : " + timeSheetId);
        }
        try {
            timeEntry.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException("TimeEntry was not created due to constraint violation");
        }
        return timeEntry.id;
    }

    @Transactional
    public Optional<Long> updateTimeEntry(Long timeSheetId, Long timeEntryId, TimeEntryRequest request, AuthenticationContext ctx) {
        logger.debug("Modify timeEntry for id={} with {}, {}", timeSheetId, request, ctx);
        return findById(timeEntryId, ctx)
                .map(timeEntry -> request.unbind(timeEntry, timeSheetId, timeSheetService::findById, ctx))
                .map(timeEntry -> timeEntry.id);
    }

    Optional<TimeEntry> findById(Long id, AuthenticationContext ctx) {
        return TimeEntry.<TimeEntry>findByIdOptional(id)
                .filter(ctx::canAccess);
    }
}