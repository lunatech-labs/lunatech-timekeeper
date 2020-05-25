package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class TimeSheetService {

    @Inject
    UserService userService;

    @Inject
    ProjectService projectService;

    private static Logger logger = LoggerFactory.getLogger(TimeSheetService.class);

    Optional<TimeSheet> findById(Long id) {
        return TimeSheet.findByIdOptional(id);
    }

    @Transactional
    Long createTimeSheet(TimeSheetRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new timesheet with {}, {}", request, ctx);
        final TimeSheet timeSheet = request.unbind(projectService::findById, userService::findById, ctx);
        try {
            timeSheet.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException(String.format("Timesheet was not created due to constraint violation"));
        }
        return timeSheet.id;
    }

}
