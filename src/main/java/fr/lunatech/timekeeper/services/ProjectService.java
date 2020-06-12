package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ProjectService {
    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    UserTransaction transaction;

    @Inject
    ClientService clientService;

    @Inject
    UserService userService;

    @Inject
    TimeSheetService timeSheetService;

    public Optional<ProjectResponse> findResponseById(Long id, Optional<Boolean> optimized, AuthenticationContext ctx) {
        return findById(id, ctx).map(project -> ProjectResponse.bind(project, optimized));
    }

    public List<ProjectResponse> listAllResponses(AuthenticationContext ctx) {
        return streamAll(ctx, ProjectResponse::bind, Collectors.toList());
    }

    @Transactional
    public Long create(ProjectRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new project with {}, {}", request, ctx);
        final Project project = request.unbind(clientService::findById, userService::findById, ctx);
        try {
            project.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException("Project was not created due to constraint violation");
        }
        project.users
                .forEach(projectUser -> timeSheetService.createDefaultTimeSheet(project, projectUser.user, ctx));
        return project.id;
    }

    public Optional<Long> update(Long id, ProjectRequest request, AuthenticationContext ctx) {
        logger.debug("Modify project for for id={} with {}, {}", id, request, ctx);
        findById(id, ctx).ifPresent(project -> {
            if (!ctx.canEdit(project)) {
                throw new ForbiddenException("The user can't edit this project with id : " + id);
            }
        });
        try {
            transaction.begin(); // See https://quarkus.io/guides/transaction API Approach
            final var maybeProject = findById(id, ctx);

            // Delete the old members
            maybeProject.ifPresent(project -> project.users.stream()
                    .filter(request::notContains)
                    .forEach(PanacheEntityBase::delete));

            final var updatedProject = findById(id, ctx)
                    .stream()
                    .map(project -> request.unbind(project, clientService::findById, userService::findById, ctx))
                    // Create the timesheets for the new members
                    .peek(project -> project.users
                            .stream()
                            .filter(projectUser -> timeSheetService.userHasNoTimeSheet(project.id, projectUser.user.id))
                            .forEach(projectUser -> timeSheetService.createDefaultTimeSheet(project, projectUser.user, ctx)))
                    .map(project -> project.id)
                    .findFirst();
            transaction.commit();
            return updatedProject;
        } catch (Throwable e) {
            // There are many various exceptions, we catch Throwable but this is arguable.
            logger.warn("Could not update a Project due to an exception {}", e.getMessage());
            try {
                transaction.rollback();
            } catch (SystemException ex) {
                logger.error("Tried to rollback in ProjectService but failed", ex);
            }
            throw new UpdateResourceException("Cannot update a Project, invalid projectRequest");
        }
    }

    // TODO : NotImplementedYet
    public Optional<Long> joinProject(Long id, Long userId, AuthenticationContext ctx) {
        logger.debug("Modify project for for id={} with userId={}, {}", id, userId, ctx);
        findById(id, ctx).ifPresent(project -> {
            if (!ctx.canJoin(project)) {
                throw new ForbiddenException("The user with id : " + userId + " can't join this project with id : " + id);
            }
        });
        return Optional.empty();
    }

    Optional<Project> findById(Long id, AuthenticationContext ctx) {
        return Project.<Project>findByIdOptional(id)
                .filter(ctx::canAccess);
    }

    <R extends Collection<ProjectResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<Project, ProjectResponse> bind,
            Collector<ProjectResponse, ?, R> collector
    ) {
        try (final Stream<Project> projects = Project.streamAll()) {
            return projects
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}
