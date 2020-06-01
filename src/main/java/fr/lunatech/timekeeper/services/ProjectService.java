package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.resources.exceptions.UpdateResourceException;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.project.ProjectResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Status;
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

    public Optional<ProjectResponse> findResponseById(Long id, AuthenticationContext ctx) {
        return findById(id, ctx).map(ProjectResponse::bind);
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
            throw new CreateResourceException(String.format("Project was not created due to constraint violation"));
        }
        return project.id;
    }

    public Optional<Long> update(Long id, ProjectRequest request, AuthenticationContext ctx)  {
        logger.debug("Modify project for for id={} with {}, {}", id, request, ctx);
        try {
            transaction.begin(); // See https://quarkus.io/guides/transaction API Approach
            final var updatedProject = findById(id, ctx)
                    .stream()
                    .map(project -> request.unbind(project, clientService::findById, userService::findById, ctx))
                    .peek(project -> project.users
                            .stream()
                            .filter(request::notContains)
                            .forEach(PanacheEntityBase::delete))
                    .map(project -> project.id)
                    .findFirst();
            transaction.commit();
            return updatedProject;
        } catch (Throwable e) {
            // There are many various exceptions, we catch Throwable but this is arguable.
            logger.warn("Could not update a Project due to an exception {}", e.getMessage());
            try {
                if (transaction.getStatus() != Status.STATUS_MARKED_ROLLBACK && transaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    transaction.rollback();
                }
            } catch (SystemException ex) {
                logger.error("Tried to rollback in ProjectService but failed",ex);
            }
            throw new UpdateResourceException("Cannot update a Project, invalid projectRequest");
        }
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
