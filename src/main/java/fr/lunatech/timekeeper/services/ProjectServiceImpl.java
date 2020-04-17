package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.interfaces.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ProjectServiceImpl implements ProjectService {

    private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    public Optional<ProjectResponse> findProjectById(Long id) {
        return Project.<Project>findByIdOptional(id).map(this::from);
    }

    @Override
    public List<ProjectResponse> listAllProjects() {
        try (final Stream<Project> projects = Project.streamAll()) {
            return projects.map(this::from).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createProject(ProjectRequest request) {
        final var project = bind(request);
        Project.persist(project);
        return project.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateProject(Long id, ProjectRequest request) {
        return Project.<Project>findByIdOptional(id).map(project -> bind(project, request).id);
    }

    private ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.id,
                project.name,
                project.billable,
                project.description,
                project.client.id,
                project.members.stream().map(m -> m.id).collect(Collectors.toList())
        );
    }

    private Project bind(Project project, ProjectRequest request) {
        project.name = request.getName();
        project.billable = request.isBillable();
        project.description = request.getDescription();
        project.client = getClient(request.getClientId());
        return project;
    }

    private Project bind(ProjectRequest request) {
        return bind(new Project(), request);
    }

    private Client getClient(Long clientId) {
        return Client.<Client>findByIdOptional(clientId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Client is required for an project. clientId=%d", clientId)));
    }
}
