package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import fr.lunatech.timekeeper.timeutils.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TimeSheetService {

    private static Logger logger = LoggerFactory.getLogger(TimeSheetService.class);

    Optional<TimeSheet> findById(Long id) {
        return TimeSheet.findByIdOptional(id);
    }

    Boolean userHasNoTimeSheet(Long projectId, Long userId){
        return TimeSheet.stream("user_id = ?1 and project_id = ?2", userId, projectId).count() == 0;
    }

    @Transactional
    Long createDefaultTimeSheet(Project project, User owner, AuthenticationContext ctx) {
        TimeSheet timeSheet = new TimeSheet(project, owner, TimeUnit.HOURLY, project.getBillable(),null,null,TimeUnit.HOURLY, Collections.emptyList());
        logger.debug("Create a default timesheet with {}", timeSheet);
        try {
            timeSheet.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException(String.format("Timesheet was not created due to constraint violation"));
        }
        return timeSheet.id;
    }

    // FIXME : It doesn't test if it is active
    public List<TimeSheetResponse> findAllActivesForUser(AuthenticationContext ctx){
        return streamAll(ctx, TimeSheetResponse::bind, Collectors.toList());
    }

    public List<TimeSheetResponse> findAllForProjectForUser(AuthenticationContext ctx, long idProject, long idUser){
        final Stream<TimeSheet> timeSheetStream = TimeSheet.stream("project_id= ?1 AND user_id= ?2", idProject, idUser);
        return timeSheetStream.map(TimeSheetResponse::bind)
                .collect(Collectors.toList());
    }


    <R extends Collection<TimeSheetResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<TimeSheet, TimeSheetResponse> bind,
            Collector<TimeSheetResponse, ?, R> collector
    ) {
        try (final Stream<TimeSheet> ts = TimeSheet.stream("user_id = ?1", ctx.getUserId())) {
            return ts
                    .map(bind)
                    .collect(collector);
        }
    }

    Optional<TimeSheet> findById(Long id, AuthenticationContext ctx) {
        return TimeSheet.<TimeSheet>findByIdOptional(id)
                .filter(timeSheet -> ctx.canAccess(timeSheet.project));
    }
}
