package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.resources.exceptions.ResourceCreationException;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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
public class ProjectService {

    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

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
    public Long create(ProjectRequest request, AuthenticationContext ctx) throws ResourceCreationException {
        logger.debug("Create a new project with {}, {}", request, ctx);
        final Project project = request.unbind(clientService::findById, userService::findById, ctx);
        try {
            project.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new ResourceCreationException(String.format("Project was not created due to constraint violation"));
        }
        return project.id;
    }

    @Transactional
    public Optional<Long> update(Long id, ProjectRequest request, AuthenticationContext ctx) {
        logger.debug("Modify project for for id={} with {}, {}", id, request, ctx);
        return findById(id, ctx)
                .stream()
                .map(project -> request.unbind(project, clientService::findById, userService::findById, ctx))
                .peek(project -> project.users
                        .stream()
                        .filter(request::notContains)
                        .forEach(PanacheEntityBase::delete))
                .map(project -> project.id)
                .findFirst();
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
