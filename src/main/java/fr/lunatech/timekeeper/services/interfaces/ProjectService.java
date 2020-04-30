package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.RoleInProjectRequest;
import fr.lunatech.timekeeper.services.dtos.RoleInProjectUpdateRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<ProjectResponse> listAllProjects();

    Long createProject(ProjectRequest request);

    Optional<ProjectResponse> findProjectById(Long id);

    Optional<Long> updateProject(Long id, ProjectRequest project);

    Long addRoleInProjectToProject(Long projectId, RoleInProjectRequest request);

    List<Long> updateRolesInProjects(Long projectId, RoleInProjectUpdateRequest request);
}
