package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.TimeSheetRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<TimeSheetResponse> listAllResponses(AuthenticationContext ctx) {
        return streamAll(ctx, TimeSheetResponse::bind, Collectors.toList());
    }

    @Transactional
    Long createTimeSheet(TimeSheetRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new timesheet with {}, {}", request, ctx);
        final TimeSheet timeSheet = request.unbind(projectService::findById, userService::findById, ctx);
        try {
            timeSheet.persistAndFlush();
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new CreateResourceException(String.format("Timesheet was not created due to constraint violation"));
        }
        return timeSheet.id;
    }

    <R extends Collection<TimeSheetResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<TimeSheet, TimeSheetResponse> bind,
            Collector<TimeSheetResponse, ?, R> collector
    ) {
        try (final Stream<TimeSheet> timesheets = TimeSheet.streamAll()) {
            return timesheets
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}