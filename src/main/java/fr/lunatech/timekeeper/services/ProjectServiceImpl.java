package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.*;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.interfaces.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ProjectServiceImpl implements ProjectService {

    private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    public Optional<ProjectResponse> findProjectById(Long organizationId, Long id) {
        return Project.<Project>findByIdOptional(id)
                //TODO .filter(checkAccess)
                .map(ProjectResponse::bind);
    }

    @Override
    public List<ProjectResponse> listAllProjects(Long organizationId) {
        try (final Stream<Project> projects = Project.streamAll()) {
            return projects
                    //TODO .filter(checkAccess)
                    .map(ProjectResponse::bind)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createProject(Long organizationId, ProjectRequest request) {
        logger.debug("Create a new project with organization={}, request={}", organizationId, request);
        final Project project = request.unbind(this::getClient, this::getUser);
        project.organization = Organization.<Organization>findByIdOptional(organizationId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown organization. organization id=%s", organizationId)));
        Project.persist(project);
        return project.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateProject(Long organizationId, Long id, ProjectRequest request) {
        logger.debug("Modify project for projectId={} with request={}", id, request);
        return Project.<Project>findByIdOptional(id)
                //TODO .filter(checkAccess)
                .map(project -> request.unbind(project, this::getClient, this::getUser))
                .map(project -> {
                    project.users.forEach(projectUser -> {
                        if (projectUserNotFoundInRequest(projectUser, request)) {
                            projectUser.delete();
                        }
                    });
                    return project.id;
                });
    }

    private boolean projectUserNotFoundInRequest(ProjectUser projectUser, ProjectRequest request) {
        return request.getUsers()
                .stream()
                .noneMatch(o -> Objects.equals(projectUser.id, o.getId())) && projectUser.isPersistent();
    }


/*
    @Transactional
    @Override
    public Long addMemberToProject(Long projectId, ProjectUserRequest request) {
        logger.debug("Add a new member for projectId={} with request={}", projectId, request);
        final var project = getProject(projectId);
        return addMemberToProject(project, request);
    }

    @Transactional
    @Override
    public List<Long> updateProjectMembers(Long projectId, MembersUpdateRequest request) {
        logger.debug("Modify members for projectId={} with request={}", projectId, request);
        final var project = getProject(projectId);

        final var membersUpdated = project.members.stream()
                .map(member -> updateOrDeleteMember(member, request))
                .filter(Optional::isPresent)
                .map(Optional::get);

        final var membersAdded = request.getMembers().stream()
                .map(memberRequest -> addMemberToProject(project, memberRequest));

        return Stream.concat(membersUpdated, membersAdded)
                .collect(Collectors.toList());
    }

    private Long addMemberToProject(Project project, ProjectUserRequest request) {
        return project.members
                .stream()
                .filter(member -> member.isSameUser(request.getUserId()))
                .findFirst()
                .map(member -> member.id)
                .orElseGet(() -> {
                    final var member = unbind(project, request);
                    ProjectUser.persist(member);
                    return member.id;
                });
    }

    private Optional<Long> updateOrDeleteMember(ProjectUser member, MembersUpdateRequest request) {
        return request.getMembers()
                .stream()
                .filter(memberRequest -> member.isSameUser(memberRequest.getUserId()))
                .findFirst()
                .map(memberRequest -> {
                    member.role = memberRequest.getRole();
                    return member.id;
                })
                .or(() -> {
                    if (member.isPersistent()) {
                        member.delete();
                    }
                    return Optional.empty();
                });
    }*/



  /*  private Project unbind(ProjectRequest request) {
        return unbind(new Project(), request);
    }

    private Project unbind(Project project, ProjectRequest request) {
        project.name = request.getName();
        project.billable = request.isBillable();
        project.description = request.getDescription();
        project.client = Optional.of(0L)
                .flatMap(this::getClient)
                .orElse(null);
        project.users = request.getUsers()
                .stream()
                .map(user -> {
                    final var projectUser = new ProjectUser();
                    projectUser.project = project;
                    projectUser.user = getUser(user.getId());
                    projectUser.manager = user.isManager();
                    return projectUser;
                })
                .collect(Collectors.toList());
        project.publicAccess = request.isPublicAccess();
        return project;
    }*/

/*
    private ProjectUser unbind(Project project, ProjectUserRequest request) {
        final var member = new ProjectUser();
        member.project = project;
        member.user = getUser(request.getUserId());
        member.role = request.getRole();
        return member;
    }
    */


    //Optional<Project> retrieveProject(Long organizationId, Long projectId) {
    //    return Project.<Project>findByIdOptional(projectId)
    //            .filter(project -> organizationId.equals(project.organization.id));
    //}
    //   .orElseThrow(() -> new IllegalEntityStateException(String.format("One project is required for member. projectId=%s", projectId)));
    // }

    private User getUser(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId=%s", userId)));
    }

    private Optional<Client> getClient(Long clientId) {
        return Client.findByIdOptional(clientId);
    }
}
