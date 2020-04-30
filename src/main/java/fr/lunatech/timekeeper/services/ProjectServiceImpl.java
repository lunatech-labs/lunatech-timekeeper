package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.services.dtos.*;
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
        return Project.<Project>findByIdOptional(id).map(this::bind);
    }

    @Override
    public List<ProjectResponse> listAllProjects() {
        try (final Stream<Project> projects = Project.streamAll()) {
            return projects.map(this::bind).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createProject(ProjectRequest request) {
        logger.debug("Create a new project with {}", request);
        final var project = unbind(request);
        project.organization = Organization.<Organization>findByIdOptional(request.getOrganizationId())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown organization. organization id=%s", request.getOrganizationId())));
        Project.persist(project);
        return project.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateProject(Long id, ProjectRequest request) {
        logger.debug("Modify project for projectId={} with request={}", id, request);
        return Project.<Project>findByIdOptional(id).map(project -> unbind(project, request).id);
    }

    @Transactional
    @Override
    public Long addRoleInProjectToProject(Long projectId, RoleInProjectRequest request) {
        logger.debug("Add a new roleInProject for projectId={} with request={}", projectId, request);
        final var project = getProject(projectId);
        return addRoleInProjectToProject(project, request);
    }

    @Transactional
    @Override
    public List<Long> updateRolesInProjects(Long projectId, RoleInProjectUpdateRequest request) {
        logger.debug("Modify rolesInProject for projectId={} with request={}", projectId, request);
        final var project = getProject(projectId);

        final var rolesInProjectsUpdated = project.members.stream()
                .map(member -> updateOrDeleteRoleInProject(member, request))
                .filter(Optional::isPresent)
                .map(Optional::get);

        final var rolesInProjectsAdded = request.getRolesInProjects().stream()
                .map(roleInProjectRequest -> addRoleInProjectToProject(project, roleInProjectRequest));

        return Stream.concat(rolesInProjectsUpdated, rolesInProjectsAdded)
                .collect(Collectors.toList());
    }

    private Long addRoleInProjectToProject(Project project, RoleInProjectRequest request) {
        return project.members
                .stream()
                .filter(member -> member.isSameUser(request.getUserId()))
                .findFirst()
                .map(member -> member.id)
                .orElseGet(() -> {
                    final var member = unbind(project, request);
                    RoleInProject.persist(member);
                    return member.id;
                });
    }

    private Optional<Long> updateOrDeleteRoleInProject(RoleInProject roleInProject, RoleInProjectUpdateRequest request) {
        return request.getRolesInProjects()
                .stream()
                .filter(roleInProjectRequest -> roleInProject.isSameUser(roleInProjectRequest.getUserId()))
                .findFirst()
                .map(roleInProjectRequest -> {
                    roleInProject.role = roleInProjectRequest.getRole();
                    return roleInProject.id;
                })
                .or(() -> {
                    if (roleInProject.isPersistent()) {
                        roleInProject.delete();
                    }
                    return Optional.empty();
                });
    }

    private ProjectResponse bind(Project project) {
        return new ProjectResponse(
                project.id,
                project.name,
                project.billable,
                project.description,
                project.client != null ? project.client.name : "",
                project.members.stream()
                        .map(roleInProject -> new RoleInProjectResponse(roleInProject.id, roleInProject.user.id, roleInProject.role, roleInProject.project.id))
                        .collect(Collectors.toList()),
                project.organization.id,
                project.publicAccess
        );
    }

    private Project unbind(Project project, ProjectRequest request) {
        project.name = request.getName();
        project.billable = request.isBillable();
        project.description = request.getDescription();
        project.client = request.getClientId().flatMap(this::getClient).orElse(null);
        project.publicAccess = request.isPublicAccess();
        return project;
    }

    private Project unbind(ProjectRequest request) {
        return unbind(new Project(), request);
    }

    private RoleInProject unbind(Project project, RoleInProjectRequest request) {
        final var roleInProject = new RoleInProject();
        roleInProject.project = project;
        roleInProject.user = getUser(request.getUserId());
        roleInProject.role = request.getRole();
        return roleInProject;
    }

    private Project getProject(Long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One project is required for roleInProject. projectId=%s", projectId)));
    }

    private User getUser(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for roleInProject. userId=%s", userId)));
    }

    private Optional<Client> getClient(Long clientId) {
        return Client.findByIdOptional(clientId);
    }
}
