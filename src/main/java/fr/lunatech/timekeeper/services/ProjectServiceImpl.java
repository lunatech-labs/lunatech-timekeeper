package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
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
    public Long addMemberToProject(Long projectId, MemberRequest request) {
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

    private Long addMemberToProject(Project project, MemberRequest request) {
        return project.members
                .stream()
                .filter(member -> member.isSameUser(request.getUserId()))
                .findFirst()
                .map(member -> member.id)
                .orElseGet(() -> {
                    final var member = unbind(project, request);
                    Member.persist(member);
                    return member.id;
                });
    }

    private Optional<Long> updateOrDeleteMember(Member member, MembersUpdateRequest request) {
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
    }


    private ProjectResponse bind(Project project) {
        return new ProjectResponse(
                project.id,
                project.name,
                project.billable,
                project.description,
                project.client != null ? project.client.name : "",
                project.members.stream()
                        .map(member -> new MemberResponse(member.id, member.user.id, member.role, member.project.id))
                        .collect(Collectors.toList()),
                project.isPublic);
    }

    private Project unbind(Project project, ProjectRequest request) {
        project.name = request.getName();
        project.billable = request.isBillable();
        project.description = request.getDescription();
        project.client = request.getClientId().flatMap(this::getClient).orElse(null);
        project.isPublic = request.getPublic();
        return project;
    }

    private Project unbind(ProjectRequest request) {
        return unbind(new Project(), request);
    }

    private Member unbind(Project project, MemberRequest request) {
        final var member = new Member();
        member.project = project;
        member.user = getUser(request.getUserId());
        member.role = request.getRole();
        return member;
    }

    private Project getProject(Long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One project is required for member. projectId=%s", projectId)));
    }

    private User getUser(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId=%s", userId)));
    }

    private Optional<Client> getClient(Long clientId) {
        return Client.findByIdOptional(clientId);
    }
}
